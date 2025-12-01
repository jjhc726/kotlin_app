package com.example.Recyclothes.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MapLoadTelemetryService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val collection = firestore.collection("mapLoadEvents")

    suspend fun logMapLoadStart(eventId: String, timestamp: Long) {
        collection.document(eventId).set(
            mapOf(
                "startTime" to timestamp,
                "endTime" to null,
                "loadTimeMs" to null
            )
        ).await()
    }

    suspend fun logMapLoadEnd(eventId: String, timestamp: Long) {
        val docRef = collection.document(eventId)

        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            val start = snapshot.getLong("startTime") ?: return@runTransaction

            val loadTime = timestamp - start

            tx.update(
                docRef, mapOf(
                    "endTime" to timestamp,
                    "loadTimeMs" to loadTime
                )
            )
        }.await()
    }
}
