package com.example.Recyclothes.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class UsageFeedbackRepository {
    private val col = Firebase.firestore.collection("usage_feedback")

    suspend fun submitOnline(email: String?, featureName: String, why: String): Boolean =
        withContext(Dispatchers.IO) {
            val payload = mapOf(
                "userEmail" to (email ?: "anonymous@local"),
                "feature"   to featureName,
                "why"       to why,
                "tsMillis"  to System.currentTimeMillis()
            )
            val ok = withTimeoutOrNull(2_500) {
                runCatching { col.add(payload).await() }.isSuccess
            }
            ok == true
        }
}
