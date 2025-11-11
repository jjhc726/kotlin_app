package com.example.Recyclothes.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UsageFeedbackRepository {
    private val col = Firebase.firestore.collection("usage_feedback")

    suspend fun submit(featureName: String, whyText: String): Boolean {
        val data = hashMapOf(
            "feature_name" to featureName,
            "why" to whyText.trim(),
            "created_at" to FieldValue.serverTimestamp()
        )
        return try {
            col.add(data).await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
