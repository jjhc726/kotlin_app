package com.example.Recyclothes.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.repository.PickupRepository
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PendingPickupsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PickupRepository(application)

    private val network = NetworkObserver(application)

    private val _pendingPickups = MutableStateFlow<List<PickupRequestEntity>>(emptyList())

    private val _networkStatus = MutableStateFlow(network.isOnline())
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    private val _networkEvents = MutableSharedFlow<NetworkEvent>(replay = 1)
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    val pendingPickups: StateFlow<List<PickupRequestEntity>> = _pendingPickups

    fun loadPendingPickups() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getPendingPickups()
            _pendingPickups.value = list
        }
    }

    fun startNetworkObserver() {
        networkCallback = network.registerCallback(
            onAvailable = {
                _networkStatus.value = true
                viewModelScope.launch { _networkEvents.emit(NetworkEvent.Available) }
            },
            onLost = {
                _networkStatus.value = false
                viewModelScope.launch { _networkEvents.emit(NetworkEvent.Lost) }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        networkCallback?.let { network.unregisterCallback(it) }
    }
}
