package com.example.Recyclothes.data.remote

import com.example.Recyclothes.data.model.FeatureId
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseUsageService {
    private val col = Firebase.firestore.collection("usage_events")

    suspend fun log(feature: FeatureId, extra: Map<String, Any?> = emptyMap()): Boolean {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous"
        val doc = hashMapOf(
            "feature" to feature.name,
            "ts" to Timestamp.now(),
            "userEmail" to email
        ) + extra.filterValues { it != null }
        return try {
            col.add(doc).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun countsSince(since: Timestamp): Map<String, Int> {
        return try {
            val snap = col.whereGreaterThanOrEqualTo("ts", since).get().await()
            val map = mutableMapOf<String, Int>()
            for (d in snap.documents) {
                val f = d.getString("feature") ?: continue
                map[f] = (map[f] ?: 0) + 1
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
