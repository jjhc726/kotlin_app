package com.example.vistaquickdonation.utils

import com.google.firebase.firestore.FirebaseFirestore

fun logDonationPreference(type: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("Engagement").document("donationPreferences")

    db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)
        val current = snapshot.getLong(type) ?: 0
        transaction.update(docRef, type, current + 1)
    }.addOnFailureListener {
        val initialData = hashMapOf(
            "pickupAtHome" to 0,
            "interactiveMap" to 0
        )
        docRef.set(initialData)
    }
}
