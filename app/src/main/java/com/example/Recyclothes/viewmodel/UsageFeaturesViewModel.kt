package com.example.Recyclothes.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.remote.FeedbackDraft
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.data.repository.FeedbackDraftRepository
import com.example.Recyclothes.data.repository.OfflineUsageFeedbackRepository
import com.example.Recyclothes.data.repository.UsageFeedbackRepository
import com.example.Recyclothes.data.repository.FeaturesAnalyticsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UsageFeaturesViewModel(app: Application) : AndroidViewModel(app) {

    private val _selected = MutableStateFlow<FeatureId?>(null)
    val selected: StateFlow<FeatureId?> = _selected

    private val _why = MutableStateFlow("")
    val why: StateFlow<String> = _why

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _submittedOk = MutableStateFlow<Boolean?>(null)
    val submittedOk: StateFlow<Boolean?> = _submittedOk

    private val _leastUsed = MutableStateFlow<List<LeastUsedUi>>(emptyList())
    val leastUsed: StateFlow<List<LeastUsedUi>> = _leastUsed

    private val onlineRepo  = UsageFeedbackRepository()
    private val offlineRepo = OfflineUsageFeedbackRepository(app)
    private val draftRepo   = FeedbackDraftRepository(app)
    private val analyticsRepo = FeaturesAnalyticsRepository()
    private val net = ConnectivityObserver(app)

    private val autoSave = MutableStateFlow(Unit)

    init {
        viewModelScope.launch {
            draftRepo.load(draftKey())?.let { d ->
                _selected.value = FeatureId.entries.find { it.label == d.featureName }
                _why.value = d.why
            }
        }

        net.onlineFlow().onEach { isOnline ->
            if (isOnline) viewModelScope.launch { offlineRepo.flushPending(onlineRepo) }
        }.launchIn(viewModelScope)

        setupAutosave()
    }

    fun loadLeastUsedThisWeek() {
        viewModelScope.launch {
            _leastUsed.value = analyticsRepo.leastUsedThisWeek(limit = 3)
        }
    }

    private fun sessionEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email
            ?: getApplication<Application>()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("email", null)
                ?.trim()
                ?.lowercase()

    private fun draftKey(): String = sessionEmail() ?: "anonymous"

    @OptIn(FlowPreview::class)
    private fun setupAutosave() {
        autoSave
            .debounce(400)
            .onEach {
                draftRepo.save(
                    draftKey(),
                    FeedbackDraft(featureName = _selected.value?.label ?: "", why = _why.value)
                )
            }
            .launchIn(viewModelScope)
    }

    fun persistDraftNow() {
        viewModelScope.launch {
            draftRepo.save(
                draftKey(),
                FeedbackDraft(featureName = _selected.value?.label ?: "", why = _why.value)
            )
        }
    }

    fun onFeatureSelected(fid: FeatureId) {
        _selected.value = fid
        autoSave.tryEmit(Unit)
    }

    fun onWhyChanged(text: String) {
        _why.value = text
        autoSave.tryEmit(Unit)
    }

    fun clearSubmittedFlag() { _submittedOk.value = null }

    fun submitFeedback(fid: FeatureId?, why: String) {
        if (fid == null || why.isBlank()) return

        viewModelScope.launch {
            _submitting.value = true
            try {
                val email = sessionEmail()
                val ok = onlineRepo.submitOnline(email, fid.label, why)
                if (ok) {
                    draftRepo.clear(draftKey())
                    _submittedOk.value = true
                } else {
                    offlineRepo.queue(email, fid.label, why)
                    _submittedOk.value = false
                }
            } finally {
                _submitting.value = false
            }
        }
    }
}

