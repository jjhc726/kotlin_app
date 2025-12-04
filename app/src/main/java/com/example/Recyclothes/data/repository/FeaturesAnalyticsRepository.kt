package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.viewmodel.LeastUsedUi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FeaturesAnalyticsRepository {
    private val col = Firebase.firestore.collection("analytics")

    suspend fun leastUsedThisWeek(limit: Int = 3): List<LeastUsedUi> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000

        val snap = runCatching {
            col.whereGreaterThan("tsMillis", sevenDaysAgo).get().await()
        }.getOrNull() ?: return@withContext emptyList()

        val counts = mutableMapOf<String, Int>()
        for (doc in snap.documents) {
            val feature = (doc.getString("feature") ?: "").trim()
            if (feature.isNotEmpty()) counts[feature] = (counts[feature] ?: 0) + 1
        }

        val all = FeatureId.entries.map { fid ->
            val c = counts[fid.label] ?: 0
            LeastUsedUi(featureId = fid, label = fid.label, weeklyCount = c)
        }

        all.sortedBy { it.weeklyCount }.take(limit)
    }
}
