package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.repository.InteractionRepository
import kotlinx.coroutines.launch

class CharityViewModel(
    private val repository: InteractionRepository = InteractionRepository()
) : ViewModel() {

    fun addInteraction() {
        viewModelScope.launch {
            repository.addCharityInteraction()
        }
    }
}
