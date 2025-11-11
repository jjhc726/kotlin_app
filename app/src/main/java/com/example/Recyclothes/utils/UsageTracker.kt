package com.example.Recyclothes.utils

import com.example.Recyclothes.data.repository.UsageRepository
import com.example.Recyclothes.data.model.FeatureId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UsageTracker {
    private val repo = UsageRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun bump(feature: FeatureId, extra: Map<String, Any?> = emptyMap()) {
        scope.launch { repo.bump(feature, extra) }
    }
}
