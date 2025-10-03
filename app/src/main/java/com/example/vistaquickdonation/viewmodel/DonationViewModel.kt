package com.example.vistaquickdonation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.DonationRepository
import com.example.vistaquickdonation.model.DonationItem
import kotlinx.coroutines.launch

class DonationViewModel : ViewModel() {

    private val repository = DonationRepository()

    fun uploadDonation(item: DonationItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.uploadDonation(item)
            onResult(success)
        }
    }
}
