package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.AppNotification
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.utils.toNotification
import com.example.Recyclothes.utils.toPretty
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = DonationRepository(application)

    private var listener: ListenerRegistration? = null

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    private val _lastDonationText = MutableStateFlow<String?>(null)
    val lastDonationText: StateFlow<String?> = _lastDonationText

    fun start(email: String) {
        listener?.remove()
        listener = repo.listenRecentDonations(email, limit = 30, onChange = { list ->
            _notifications.value = list.map { it.toNotification() }

            val newest = list.maxByOrNull { it.createdAt?.toDate()?.time ?: 0L }
            _lastDonationText.value = newest?.createdAt?.toPretty()
        })

        viewModelScope.launch {
            if (_lastDonationText.value == null) {
                val ts = repo.getLastDonationTimestamp(email)
                _lastDonationText.value = ts?.toPretty()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
