package com.example.Recyclothes.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.util.Log
import android.util.SparseArray
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.data.repository.EngagementRepository
import com.example.Recyclothes.data.repository.PickupRepository
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class NetworkEvent {
    object Available : NetworkEvent()
    object Lost : NetworkEvent()
}

class PickupViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PickupRepository(application)
    private val donationRepo = DonationRepository(application)
    private val engagementRepo = EngagementRepository()

    private val network = NetworkObserver(application)

    private val userRepo = UserRepository()

    val address = MutableStateFlow("")
    val date = MutableStateFlow("")
    val hour = MutableStateFlow("")
    val cause = MutableStateFlow("")
    private val _donations = MutableStateFlow<SparseArray<DonationItem>>(SparseArray())
    val donations: StateFlow<SparseArray<DonationItem>> = _donations

    private val _networkStatus = MutableStateFlow(network.isOnline())
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    private val _networkEvents = MutableSharedFlow<NetworkEvent>(replay = 1)
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun loadUserDonations() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = userRepo.currentEmail() ?: return@launch

            val list = donationRepo.getUserDonations(email)

            val sparse = SparseArray<DonationItem>()
            list.forEachIndexed { index, item ->
                sparse.put(index, item)
            }

            _donations.value = sparse
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

    fun getCurrentUserEmail(): String {
        return userRepo.currentEmail() ?: ""
    }


    fun submitPickup(
        donationId: String,
        userId: String,
        onNoConnection: () -> Unit = {},
        onFinished: () -> Unit = {}
    ) {

        val pickup = PickupRequestEntity(
            donationId = donationId,
            userId = userId,
            address = address.value,
            date = date.value,
            hour = hour.value,
            isCompleted = false
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
