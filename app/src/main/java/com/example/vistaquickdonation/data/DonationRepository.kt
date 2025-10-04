package com.example.vistaquickdonation.data

import com.example.vistaquickdonation.model.DonationItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class DonationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val donations = firestore.collection("donations")

    suspend fun uploadDonation(item: DonationItem): Boolean {
        return try {
            donations.add(item).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadDonationWithTimestamp(item: DonationItem): Boolean {
        return try {
            val data = hashMapOf(
                "description"   to item.description,
                "clothingType"  to item.clothingType,
                "size"          to item.size,
                "brand"         to item.brand,
                "createdAt"     to FieldValue.serverTimestamp()
            )
            donations.add(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun getLatestDonations(limit: Long = 20) =
        donations
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
}
