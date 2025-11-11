package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.data.repository.UsageFeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.Recyclothes.data.repository.UsageRepository

data class LeastUsedUi(
    val featureId: FeatureId,
    val label: String,
    val weeklyCount: Int
)

class UsageFeaturesViewModel(
    private val usageRepo: UsageRepository = UsageRepository(),
    private val feedbackRepo: UsageFeedbackRepository = UsageFeedbackRepository()
) : ViewModel() {

    private val _leastUsed = MutableStateFlow<List<LeastUsedUi>>(emptyList())
    val leastUsed: StateFlow<List<LeastUsedUi>> = _leastUsed

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _submittedOk = MutableStateFlow<Boolean?>(null)
    val submittedOk: StateFlow<Boolean?> = _submittedOk

    fun loadLeastUsedThisWeek() = viewModelScope.launch {
        val weekly: Map<FeatureId, Int> = usageRepo.weeklyCounts()
        val three = weekly.entries
            .sortedBy { it.value }
            .take(3)
            .map { LeastUsedUi(it.key, it.key.label, it.value) }
        _leastUsed.value = three
    }

    fun submitFeedback(selectedId: FeatureId, why: String) = viewModelScope.launch {
        _submitting.value = true
        val ok = feedbackRepo.submit(selectedId.label, why)
        _submitting.value = false
        _submittedOk.value = ok
    }

    fun clearSubmittedFlag() { _submittedOk.value = null }
}
