package com.example.Recyclothes.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.data.repository.InteractionRepository
import kotlinx.coroutines.launch

class CharityViewModel(
    private val repository: InteractionRepository = InteractionRepository()
) : ViewModel() {

    val charities = listOf(
        Charity(
            1, "Helping Hands",
            "Provides support and resources to families in need.",
            listOf("Food donations", "Clothing drive", "Medical assistance")
        ),
        Charity(
            2, "EcoFuture",
            "Promotes environmental sustainability through education and action.",
            listOf("Tree planting", "Recycling campaigns", "Beach cleanups")
        ),
        Charity(
            3, "AnimalCare",
            "Rescues and rehabilitates abandoned animals.",
            listOf("Adoption fairs", "Sterilization campaigns", "Pet food collection")
        )
    )

    var selectedCharity by mutableStateOf<Charity?>(null)
        private set

    fun selectCharity(charity: Charity) {
        selectedCharity = charity
    }

    fun goBack() {
        selectedCharity = null
    }

    fun addInteraction() {
        viewModelScope.launch {
            repository.addCharityInteraction()
        }
    }
}
