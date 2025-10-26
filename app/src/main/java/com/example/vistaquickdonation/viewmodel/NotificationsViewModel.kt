package com.example.vistaquickdonation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.repository.DonationRepository
import com.example.vistaquickdonation.data.model.DonationItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class AppNotification(
    val title: String,
    val body: String,
    val whenMillis: Long
)

class NotificationsViewModel(
    private val repo: DonationRepository = DonationRepository()
) : ViewModel() {

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

private fun DonationItem.toNotification(): AppNotification {
    val millis = (createdAt ?: Timestamp.now()).toDate().time
    val whenText = (createdAt ?: Timestamp.now()).toPretty()
    val title = "Donation successful"
    val body = buildString {
        append(description.ifBlank { clothingType.ifBlank { "Clothes" } })
        append(" — ")
        append(whenText)
    }
    return AppNotification(title, body, millis)
}

private fun Timestamp.toPretty(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}
