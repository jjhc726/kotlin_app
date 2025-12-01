package com.example.Recyclothes.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EngagementServiceAdapter(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val docRef = firestore.collection("Engagement").document("donationPreferences")

    suspend fun increment(field: String) {
        docRef.update(field, FieldValue.increment(1))
            .addOnFailureListener {
                docRef.set(mapOf(
                    "pickupAtHome" to 0,
                    "scheduleDonation" to 0
                ))
                docRef.update(field, FieldValue.increment(1))
            }
            .await()
    }

    suspend fun incrementPickupAtHome() = increment("pickupAtHome")

    suspend fun incrementScheduleDonation() = increment("scheduleDonation")
}
