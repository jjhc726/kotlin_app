package com.example.vistaquickdonation.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class InteractionService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collectionName = "interactions"

    suspend fun incrementCampaignInteraction() {
        val docRef = firestore.collection(collectionName).document("campaignsInteractions")
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("count") ?: 0L
            transaction.set(docRef, mapOf("count" to currentCount + 1))
        }.await()
    }

    suspend fun incrementCharityInteraction() {
        val docRef = firestore.collection(collectionName).document("charitiesInteractions")
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("count") ?: 0L
            transaction.set(docRef, mapOf("count" to currentCount + 1))
        }.await()
    }
}
