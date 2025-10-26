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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TopDonor(
    val name: String,
    val totalDonations: Int
)

class DonationViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = DonationRepository()

    val capturedImage = mutableStateOf<Bitmap?>(null)
    val description = mutableStateOf("")
    val clothingType = mutableStateOf("")
    val size = mutableStateOf("")
    val brand = mutableStateOf("")

    private val _topDonors = MutableStateFlow<List<TopDonor>>(emptyList())
    val topDonors: StateFlow<List<TopDonor>> = _topDonors.asStateFlow()

    private var recentListener: ListenerRegistration? = null

    private val userRepo = UserRepository()

    private fun sessionEmail(): String? = userRepo.currentEmail()
    fun uploadDonation(onResult: (Boolean) -> Unit) {
        val donation = DonationItem(
            description = description.value,
            clothingType = clothingType.value,
            size = size.value,
            brand = brand.value
        )
        makeDonation(donation, onResult)
    }

    private fun makeDonation(item: DonationItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val email = sessionEmail()
            if (email.isNullOrEmpty()) {
                onResult(false)
                return@launch
            }
            val ok = repository.uploadDonation(item, userEmail = email)
            onResult(ok)
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
        recentListener = null
        super.onCleared()
    }
}
