package com.example.vistaquickdonation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.model.DonationItem
import com.example.vistaquickdonation.data.repository.DonationRepository
import com.example.vistaquickdonation.data.repository.UserRepository
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
        val firstError = validateInputs()
        if (firstError != null) {
            onResult(false, false, firstError)
            return
        }

        val donation = DonationItem(
            description = description.value.trim(),
            clothingType = clothingType.value,
            size = size.value.trim(),
            brand = brand.value.trim(),
            tags = selectedTags.value
        )

        viewModelScope.launch(Dispatchers.IO) {
            val email = sessionEmail()
            if (email.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    onResult(false, false, "User session not found.")
                }
                return@launch
            }

            try {
                val uploadDonation = repository.uploadDonation(donation, userEmail = email)
                val ok = uploadDonation
                withContext(Dispatchers.Main) {
                    if (ok) {
                        onResult(true, false, "Donation stored successfully!")
                    } else {
                        onResult(false, true, "No internet connection. Donation saved locally.")
                    }
                }
            } catch (_: Exception) {
                repository.syncPendingDonations()
                withContext(Dispatchers.Main) {
                    onResult(false, true, "No internet connection. Donation saved locally.")
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
