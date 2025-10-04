package com.example.vistaquickdonation.data

import com.example.vistaquickdonation.model.DonationItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar

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
    suspend fun getThisMonthDonations() =
        try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val startOfMonth = Timestamp(calendar.time)

            donations
                .whereGreaterThanOrEqualTo("createdAt", startOfMonth)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}
