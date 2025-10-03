package com.example.vistaquickdonation.data

import com.example.vistaquickdonation.model.DonationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DonationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadDonation(item: DonationItem): Boolean {
        return try {
            firestore.collection("donations").add(item).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
