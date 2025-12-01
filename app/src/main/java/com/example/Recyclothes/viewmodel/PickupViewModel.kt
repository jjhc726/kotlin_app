package com.example.Recyclothes.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.model.DonationPoint
import com.example.Recyclothes.data.repository.CharitiesRepository
import com.example.Recyclothes.data.repository.EngagementRepository
import com.example.Recyclothes.data.repository.PickupRepository
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class NetworkEvent {
    object Available : NetworkEvent()
    object Lost : NetworkEvent()
}

class PickupViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PickupRepository(application)
    private val charitiesRepo = CharitiesRepository(application)

    private val engagementRepo = EngagementRepository()

    private val network = NetworkObserver(application)

    private val userRepo = UserRepository()

    val address = MutableStateFlow("")
    val date = MutableStateFlow("")
    val hour = MutableStateFlow("")
    val cause = MutableStateFlow("")

    private val _charities = MutableStateFlow<List<DonationPoint>>(emptyList())
    fun filteredCharities(): StateFlow<List<DonationPoint>> =
        _charities.combine(cause) { list, c -> list.filter { it.cause.equals(c, ignoreCase = true) } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // network status exposed
    private val _networkStatus = MutableStateFlow(network.isOnline())
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    private val _networkEvents = MutableSharedFlow<NetworkEvent>(replay = 1)
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun loadCharities() {
        viewModelScope.launch(Dispatchers.IO) {
            // load local cache first
            val local = charitiesRepo.getLocalDonationPoints()
            _charities.value = local

            // try refresh remote if online
            if (network.isOnline()) {
                try {
                    val remote = charitiesRepo.getRemoteAndCache()
                    _charities.value = remote
                } catch (e: Exception) {
                    // ignore remote failures, keep cached data
                }
            }
        }
    }

    /**
     * Starts network observer and emits events to UI. You can call this from a composable LaunchedEffect once.
     */
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

    fun getCurrentUserEmail(): String {
        return userRepo.currentEmail() ?: ""
    }


    /**
     * Submit pickup. onNoConnection() and onFinished() are invoked on the main thread.
     */
    fun submitPickup(
        foundationId: String,
        userId: String,
        onNoConnection: () -> Unit = {},
        onFinished: () -> Unit = {}
    ) {
        val pickup = PickupRequestEntity(
            userId = userId,
            address = address.value,
            date = date.value,
            hour = hour.value,
            cause = cause.value,
            foundationId = foundationId
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (network.isOnline()) {
                try {
                    repo.sendPickupOnline(pickup)
                    withContext(Dispatchers.Main) { onFinished() }
                } catch (e: Exception) {
                    repo.savePickupOffline(pickup)
                    withContext(Dispatchers.Main) { onFinished() }
                }
            } else {
                repo.savePickupOffline(pickup)
                withContext(Dispatchers.Main) { onNoConnection() }
            }
        }
    }

    fun onPickupAtHomeSelected() {
        viewModelScope.launch {
            engagementRepo.logPickupAtHome()
        }
    }

    fun resetForm() {
        address.value = ""
        date.value = ""
        hour.value = ""
        cause.value = ""
    }

}
