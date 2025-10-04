package com.example.vistaquickdonation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.DonationRepository
import com.example.vistaquickdonation.model.DonationItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DonationViewModel : ViewModel() {

    private val repository = DonationRepository()

    private val _monthlyDonations = MutableStateFlow<List<DonationItem>>(emptyList())
    val monthlyDonations: StateFlow<List<DonationItem>> = _monthlyDonations

    fun uploadDonation(item: DonationItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.uploadDonation(item)
            onResult(success)
        }
    }

    /** 🔹 Nuevo: cargar donaciones del mes */
    fun loadThisMonthDonations() {
        viewModelScope.launch {
            val snapshot = repository.getThisMonthDonations()
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(DonationItem::class.java)
            } ?: emptyList()
            _monthlyDonations.value = items
        }
    }
}
