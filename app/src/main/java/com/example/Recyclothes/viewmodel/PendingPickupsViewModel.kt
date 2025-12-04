package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.repository.PickupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PendingPickupsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PickupRepository(application)

    private val _pendingPickups = MutableStateFlow<List<PickupRequestEntity>>(emptyList())
    val pendingPickups: StateFlow<List<PickupRequestEntity>> = _pendingPickups

    fun loadPendingPickups() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getPendingPickups()
            _pendingPickups.value = list
        }
    }
}
