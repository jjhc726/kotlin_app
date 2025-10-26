package com.example.vistaquickdonation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaquickdonation.data.repository.DonationRepository
import com.example.vistaquickdonation.data.model.DonationItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class DonationViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = DonationRepository()

    private val _monthlyDonations = MutableStateFlow<List<DonationItem>>(emptyList())
    val monthlyDonations: StateFlow<List<DonationItem>> = _monthlyDonations.asStateFlow()

    private var recentListener: ListenerRegistration? = null

    private fun sessionEmail(): String? =
        getApplication<Application>()
            .getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("email", null)
            ?.trim()
            ?.lowercase()

    fun uploadDonation(item: DonationItem, onResult: (Boolean) -> Unit) {
        makeDonation(item, onResult)
    }

    fun makeDonation(item: DonationItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val email = sessionEmail()
            if (email.isNullOrEmpty()) {
                onResult(false)
                return@launch
            }
            val ok = repository.uploadDonation(item, userEmail = email)
            onResult(ok)
        }
    }

    fun loadThisMonthDonations() {
        recentListener?.remove()
        recentListener = null

        viewModelScope.launch {
            val email = sessionEmail() ?: run {
                _monthlyDonations.value = emptyList()
                return@launch
            }

            recentListener = repository.listenRecentDonations(
                userEmail = email,
                limit = 5,
                onChange = { list ->
                    val now = Calendar.getInstance()
                    val currYear = now.get(Calendar.YEAR)
                    val currMonth = now.get(Calendar.MONTH)

                    val monthList = list.filter { item ->
                        val date = (item.createdAt ?: Timestamp.now()).toDate()
                        val cal = Calendar.getInstance().apply { time = date }
                        cal.get(Calendar.YEAR) == currYear &&
                                cal.get(Calendar.MONTH) == currMonth
                    }
                    _monthlyDonations.value = monthList
                },
                onError = {
                }
            )
        }
    }

    override fun onCleared() {
        recentListener?.remove()
        recentListener = null
        super.onCleared()
    }
}
