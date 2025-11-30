package com.example.Recyclothes.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.DonationEntity
import com.example.Recyclothes.data.local.DraftDonationEntity
import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.utils.NetworkObserver
import com.example.Recyclothes.utils.PreferenceHelper
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TopDonor(
    val name: String,
    val totalDonations: Int
)

class DonationViewModel(
    app: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(app) {

    private val repository = DonationRepository(app.applicationContext)
    private val userRepo = UserRepository()
    private val prefs = PreferenceHelper(app.applicationContext)

    // Load persistent data
    val capturedImage = state.getStateFlow<Bitmap?>(
        "capturedImage",
        prefs.loadBitmap("capturedImage")
    )
    val description = state.getStateFlow(
        "description",
        prefs.load("description")
    )
    val clothingType = state.getStateFlow(
        "clothingType",
        prefs.load("clothingType")
    )
    val size = state.getStateFlow(
        "size",
        prefs.load("size")
    )
    val brand = state.getStateFlow(
        "brand",
        prefs.load("brand")
    )
    val selectedTags = state.getStateFlow<List<String>>(
        "selectedTags",
        prefs.loadList("selectedTags")
    )

    // Save updates
    fun updateImage(img: Bitmap?) {
        state["capturedImage"] = img
        prefs.saveBitmap("capturedImage", img)
    }

    fun updateDescription(text: String) {
        state["description"] = text
        prefs.save("description", text)
    }

    fun updateClothingType(text: String) {
        state["clothingType"] = text
        prefs.save("clothingType", text)
    }

    fun updateSize(text: String) {
        state["size"] = text
        prefs.save("size", text)
    }

    fun updateBrand(text: String) {
        state["brand"] = text
        prefs.save("brand", text)
    }

    fun toggleTag(tag: String) {
        val updated =
            if (selectedTags.value.contains(tag))
                selectedTags.value - tag
            else
                selectedTags.value + tag

        state.set("selectedTags", updated)
        prefs.saveList("selectedTags", updated)
    }

    val descriptionError = mutableStateOf<String?>(null)
    val clothingTypeError = mutableStateOf<String?>(null)
    val sizeError = mutableStateOf<String?>(null)
    val brandError = mutableStateOf<String?>(null)
    val imageError = mutableStateOf<String?>(null)

    private val _topDonors = MutableStateFlow<List<TopDonor>>(emptyList())
    val topDonors: StateFlow<List<TopDonor>> = _topDonors.asStateFlow()

    private var recentListener: ListenerRegistration? = null

    private var editingDraftId: Long? = null

    val availableTags = listOf(
        "Women", "Men", "Kids", "Winter", "Summer",
        "Formal", "Casual", "Sport", "Party", "Coat"
    )

    private fun sessionEmail(): String? = userRepo.currentEmail()

    private fun validateInputs(): String? {
        descriptionError.value = null
        clothingTypeError.value = null
        sizeError.value = null
        brandError.value = null
        imageError.value = null

        if (capturedImage.value == null) {
            imageError.value = "You must capture an image"
            return imageError.value
        }

        descriptionError.value = when {
            description.value.isBlank() -> "Description is required"
            description.value.length < 10 -> "Must be at least 10 characters"
            description.value.length > 200 -> "Too long (max 200 characters)"
            else -> null
        }
        if (descriptionError.value != null) return descriptionError.value

        clothingTypeError.value =
            if (clothingType.value.isBlank()) "Select a clothing type" else null
        if (clothingTypeError.value != null) return clothingTypeError.value

        sizeError.value =
            if (size.value.isBlank()) "Size is required" else null
        if (sizeError.value != null) return sizeError.value

        brandError.value =
            if (brand.value.isBlank()) "Brand is required" else null
        if (brandError.value != null) return brandError.value

        return null
    }

    fun uploadDonation(onResult: (Boolean, Boolean, String) -> Unit) {
        val error = validateInputs()
        if (error != null) {
            onResult(false, false, error)
            return
        }

        val email = sessionEmail()
        if (email.isNullOrEmpty()) {
            onResult(false, false, "User session not found.")
            return
        }

        val localDonation = DonationEntity(
            description = description.value.trim(),
            clothingType = clothingType.value,
            size = size.value.trim(),
            brand = brand.value.trim(),
            tags = selectedTags.value.joinToString(","),
            userEmail = email
        )

        val donationItem = DonationItem(
            description = localDonation.description,
            clothingType = localDonation.clothingType,
            size = localDonation.size,
            brand = localDonation.brand,
            tags = selectedTags.value,
            userEmail = email
        )

        val net = NetworkObserver(getApplication()).isOnline()

        viewModelScope.launch(Dispatchers.IO) {
            if (!net) {
                repository.insertPending(localDonation)
                withContext(Dispatchers.Main) {
                    clearAll()
                    onResult(true, true, "Donation saved offline.")
                }

                return@launch
            }


            try {
                val uploaded = repository.uploadDonation(donationItem, email)

                withContext(Dispatchers.Main) {
                    if (uploaded) {
                        clearAll()
                        onResult(true, false, "Donation uploaded successfully.")
                    } else {
                        onResult(true, true, "Donation saved offline.")
                    }
                }

            } catch (e: Exception) {
                repository.insertPending(localDonation)

                withContext(Dispatchers.Main) {
                    onResult(true, true, "Donation saved offline.")
                }
            }
        }
    }

    fun clearAll() {
        prefs.clearAll()

        state["capturedImage"] = null
        state["description"] = ""
        state["clothingType"] = ""
        state["size"] = ""
        state["brand"] = ""
        state["selectedTags"] = emptyList<String>()
    }

    fun loadTopDonors() {
        viewModelScope.launch {
            repository.getTopDonors(
                limit = 5,
                onSuccess = { topList -> _topDonors.value = topList },
                onError = { _topDonors.value = emptyList() }
            )
        }
    }

    override fun onCleared() {
        recentListener?.remove()
        super.onCleared()
    }

    fun loadDraft(id: Long) {
        viewModelScope.launch {
            val draft = repository.getDraftById(id)
            draft?.let {
                editingDraftId = it.id
                state["description"] = it.description
                state["clothingType"] = it.clothingType
                state["size"] = it.size
                state["brand"] = it.brand
                state["selectedTags"] = it.tags.split(",")
            }
        }
    }

    fun saveDraft() {
        val draft = DraftDonationEntity(
            id = editingDraftId ?: 0,
            description = description.value.trim(),
            clothingType = clothingType.value,
            size = size.value.trim(),
            brand = brand.value.trim(),
            tags = selectedTags.value.joinToString(","),
            userEmail = sessionEmail() ?: ""
        )

        viewModelScope.launch {
            if (editingDraftId == null)
                editingDraftId = repository.insertDraft(draft)
            else
                repository.updateDraft(draft)
        }
    }
}
