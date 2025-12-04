package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.AppRatingDraft
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class AppRatingRepository {
    private val col = Firebase.firestore.collection("App_rating")

    suspend fun submit(email: String?, draft: AppRatingDraft): Boolean = withContext(Dispatchers.IO) {
        val payload = hashMapOf(
            "userEmail"   to (email ?: "anonymous@local"),
            "stars"       to draft.stars,
            "likeMost"    to draft.likeMost,
            "improvements" to draft.improvements,
            "recommend"   to draft.recommend,
            "comments"    to draft.comments,
            "createdAt"   to Timestamp.now()
        )
        val ok = withTimeoutOrNull(5_000) {
            col.add(payload).await(); true
        }
        ok ?: false
    }
}
