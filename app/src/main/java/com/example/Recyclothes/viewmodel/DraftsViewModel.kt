package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.model.ScheduleDonationDraftRdb
import com.example.Recyclothes.data.model.UsageFeedbackDraftRdb
import com.example.Recyclothes.data.repository.ScheduleDonationDraftsRdbRepository
import com.example.Recyclothes.data.repository.UsageFeedbackDraftsRdbRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DraftsViewModel(app: Application) : AndroidViewModel(app) {

    private val schedRepo = ScheduleDonationDraftsRdbRepository(app)
    private val usageRepo = UsageFeedbackDraftsRdbRepository(app)
    private val net = ConnectivityObserver(app)

    private val _sched = MutableStateFlow<List<ScheduleDonationDraftRdb>>(emptyList())
    val sched: StateFlow<List<ScheduleDonationDraftRdb>> = _sched

    private val _usage = MutableStateFlow<List<UsageFeedbackDraftRdb>>(emptyList())
    val usage: StateFlow<List<UsageFeedbackDraftRdb>> = _usage

    init {
        refresh()
        net.onlineFlow().onEach { if (it) viewModelScope.launch {
            schedRepo.flushPendingToFirebase()
            usageRepo.flushPendingToFirebase()
        } }.launchIn(viewModelScope)
    }

    private fun email(): String =
        FirebaseAuth.getInstance().currentUser?.email ?: "anonymous@local"

    fun refresh() {
        val e = email()
        _sched.value = schedRepo.listAll(e)
        _usage.value = usageRepo.listAll(e)
    }

    fun deleteScheduleDraft(id: String) {
        schedRepo.deleteById(id)
        refresh()
    }

    fun deleteUsageDraft(id: String) {
        usageRepo.deleteById(id)
        refresh()
    }
}
