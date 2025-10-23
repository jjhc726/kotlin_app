package com.example.vistaquickdonation.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class DonationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val donations = firestore.collection("donations")


    suspend fun uploadDonation(item: DonationItem, userEmail: String): Boolean {
        return try {
            val data = hashMapOf(
                "description" to item.description,
                "clothingType" to item.clothingType,
                "size" to item.size,
                "brand" to item.brand,
                "userEmail" to userEmail.trim().lowercase(),
                "createdAt" to FieldValue.serverTimestamp()
            )
            donations.add(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun listenRecentDonations(
        userEmail: String,
        limit: Int = 5,
        onChange: (List<DonationItem>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        val key = userEmail.trim().lowercase()

        return donations
            .whereEqualTo("userEmail", key)
            .addSnapshotListener { snap, e ->
                if (e != null) { onError(e); return@addSnapshotListener }

                val list = snap?.documents?.map { doc ->
                    DonationItem(
                        description = doc.getString("description") ?: "",
                        clothingType = doc.getString("clothingType") ?: "",
                        size = doc.getString("size") ?: "",
                        brand = doc.getString("brand") ?: "",
                        userEmail = doc.getString("userEmail") ?: "",
                        createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                    )
                }.orEmpty()
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                    .take(limit)

                onChange(list)
            }
    }



    suspend fun getLastDonationTimestamp(userEmail: String): Timestamp? {
        val key = userEmail.trim().lowercase()


        val q = donations
            .whereEqualTo("userEmail", key)
            .get()
            .await()

        return q.documents
            .mapNotNull { it.getTimestamp("createdAt") }
            .maxByOrNull { it.toDate().time }
    }

}
