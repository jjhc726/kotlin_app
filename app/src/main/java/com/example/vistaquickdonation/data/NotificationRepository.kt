package com.example.vistaquickdonation.data

import com.example.vistaquickdonation.model.AppNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun col() = db.collection("inbox").document("device").collection("notifications")

    fun streamNotifications() = callbackFlow<List<AppNotification>> {
        val reg = col()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { d ->
                    val n = d.toObject(AppNotification::class.java)
                    n?.copy(id = d.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun markAllRead() {
        val batch = db.batch()
        val docs = col().get().await()
        docs.forEach { d -> batch.update(d.reference, "read", true) }
        batch.commit().await()
    }
}
