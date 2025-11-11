package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.FirebaseUsageService
import com.example.Recyclothes.data.model.FeatureId
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class UsageRepository(
    private val service: FirebaseUsageService = FirebaseUsageService()
) {
    suspend fun bump(feature: FeatureId, extra: Map<String, Any?> = emptyMap()): Boolean =
        withContext(Dispatchers.IO) { service.log(feature, extra) }


    suspend fun weeklyCounts(): Map<FeatureId, Int> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
        val since = Timestamp(cal.time)
        val raw = service.countsSince(since)
        val result = mutableMapOf<FeatureId, Int>().apply {
            FeatureId.entries.forEach { put(it, 0) }
        }
        raw.forEach { (k, v) ->
            runCatching { FeatureId.valueOf(k) }.getOrNull()?.let { result[it] = v }
        }
        result
    }
}
