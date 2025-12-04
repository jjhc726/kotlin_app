package com.example.Recyclothes.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.data.remote.FeedbackDraft
import com.example.Recyclothes.data.repository.FeedbackDraftRepository
import com.example.Recyclothes.data.repository.OfflineUsageFeedbackRepository
import com.example.Recyclothes.data.repository.UsageFeedbackRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class LeastUsedUi(
    val featureId: FeatureId,
    val label: String,
    val weeklyCount: Int
)

class UsageFeaturesViewModel(app: Application) : AndroidViewModel(app) {

    private val _leastUsed = MutableStateFlow<List<LeastUsedUi>>(emptyList())
    val leastUsed: StateFlow<List<LeastUsedUi>> = _leastUsed


    fun loadLeastUsedThisWeek() {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                val now = System.currentTimeMillis()
                val weekAgo = now - 7L * 24 * 60 * 60 * 1000
                val db = Firebase.firestore

                suspend fun queryCounts(collection: String): Map<String, Int> {
                    val snap = runCatching {
                        db.collection(collection)
                            .whereGreaterThanOrEqualTo("tsMillis", weekAgo)
                            .get()
                            .await()
                    }.getOrNull() ?: return emptyMap()

                    val map = mutableMapOf<String, Int>()
                    for (doc in snap.documents) {
                        val name = (doc.getString("feature") ?: doc.getString("feature_name")) ?: continue
                        map[name] = (map[name] ?: 0) + 1
                    }
                    return map
                }

                val counts = queryCounts("analytics").ifEmpty { queryCounts("usage_events") }

                return@withContext counts.entries
                    .sortedBy { it.value }
                    .take(3)
                    .map { (label, c) ->
                        val fid = FeatureId.entries.find { it.label == label } ?: FeatureId.HOME_OPEN
                        LeastUsedUi(featureId = fid, label = label, weeklyCount = c)
                    }
            }

            _leastUsed.value = data
        }
    }


    private val _selected = MutableStateFlow<FeatureId?>(null)
    val selected: StateFlow<FeatureId?> = _selected

    private val _why = MutableStateFlow("")
    val why: StateFlow<String> = _why

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _submittedOk = MutableStateFlow<Boolean?>(null)
    val submittedOk: StateFlow<Boolean?> = _submittedOk

    private val onlineRepo  = UsageFeedbackRepository()
    private val offlineRepo = OfflineUsageFeedbackRepository(app)
    private val draftRepo   = FeedbackDraftRepository(app)
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

    fun onFeatureSelected(fid: FeatureId) {
        _selected.value = fid
        autoSave.tryEmit(Unit)
    }

    fun onWhyChanged(text: String) {
        _why.value = text
        autoSave.tryEmit(Unit)
    }

    fun persistDraftNow() {
        draftRepo.save(
            draftKey(),
            FeedbackDraft(featureName = _selected.value?.label ?: "", why = _why.value)
        )
    }

    fun clearSubmittedFlag() { _submittedOk.value = null }

    fun submitFeedback(fid: FeatureId?, why: String) {
        if (fid == null || why.isBlank()) return
        viewModelScope.launch {
            _submitting.value = true
            val email = sessionEmail()
            val ok = onlineRepo.submitOnline(email, fid.label, why)
            if (ok) {
                _submittedOk.value = true
                resetFormAndClearDraft()
            } else {
                offlineRepo.queue(email, fid.label, why)
                _submittedOk.value = false
                resetFormAndClearDraft()
            }
            _submitting.value = false
        }
    }

    private fun resetFormAndClearDraft() {
        draftRepo.clear(draftKey())
        _selected.value = null
        _why.value = ""
        autoSave.tryEmit(Unit)
    }
}
