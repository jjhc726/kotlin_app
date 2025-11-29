package com.example.Recyclothes.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.DonationEntity
import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.utils.NetworkObserver
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

class DonationViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = DonationRepository(app.applicationContext)
    private val userRepo = UserRepository()

    val capturedImage = mutableStateOf<Bitmap?>(null)
    val description = mutableStateOf("")
    val clothingType = mutableStateOf("")
    val size = mutableStateOf("")
    val brand = mutableStateOf("")
    val selectedTags = mutableStateOf<List<String>>(emptyList())

    val descriptionError = mutableStateOf<String?>(null)
    val clothingTypeError = mutableStateOf<String?>(null)
    val sizeError = mutableStateOf<String?>(null)
    val brandError = mutableStateOf<String?>(null)
    val imageError = mutableStateOf<String?>(null)

    private val _topDonors = MutableStateFlow<List<TopDonor>>(emptyList())
    val topDonors: StateFlow<List<TopDonor>> = _topDonors.asStateFlow()

    private var recentListener: ListenerRegistration? = null

    val availableTags = listOf(
        "Women", "Men", "Kids", "Winter", "Summer",
        "Formal", "Casual", "Sport", "Party", "Coat"
    )

    private fun sessionEmail(): String? = userRepo.currentEmail()

    fun toggleTag(tag: String) {
        selectedTags.value = if (selectedTags.value.contains(tag)) {
            selectedTags.value - tag
        } else {
            selectedTags.value + tag
        }
    }

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

        sizeError.value = if (size.value.isBlank()) "Size is required" else null
        if (sizeError.value != null) return sizeError.value

        brandError.value = if (brand.value.isBlank()) "Brand is required" else null
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
                // Guardado local inmediato
                repository.insertPending(localDonation)

                withContext(Dispatchers.Main) {
                    onResult(true, true, "Donation saved offline.")
                }
                return@launch
            }

            try {
                val uploaded = repository.uploadDonation(donationItem, email)

                withContext(Dispatchers.Main) {
                    if (uploaded) {
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
}
