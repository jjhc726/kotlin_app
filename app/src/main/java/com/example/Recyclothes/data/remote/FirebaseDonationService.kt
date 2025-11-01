package com.example.Recyclothes.data.remote

import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.viewmodel.TopDonor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirebaseDonationService(
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
                "tags" to item.tags,
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
                if (e != null) {
                    onError(e); return@addSnapshotListener
                }

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

    fun getTopDonors(
        limit: Int = 5,
        onSuccess: (List<TopDonor>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("donations")
            .get()
            .addOnSuccessListener { snapshot ->
                val counts = snapshot.documents
                    .groupBy { it.getString("userEmail") ?: "" }
                    .mapValues { (_, docs) -> docs.size }
                    .toList()
                    .sortedByDescending { it.second }
                    .take(limit)

                if (counts.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val usersRef = db.collection("users")
                val result = mutableListOf<TopDonor>()

                var completed = 0
                val pending = counts.size

                counts.forEach { (email, total) ->
                    usersRef
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { query ->
                            val userDoc = query.documents.firstOrNull()
                            val name = userDoc?.getString("name") ?: email

                            result.add(TopDonor(name = name, totalDonations = total))

                            completed++
                            if (completed == pending) {
                                onSuccess(result.sortedByDescending { it.totalDonations })
                            }
                        }
                        .addOnFailureListener {
                            completed++
                            if (completed == pending) {
                                onSuccess(result.sortedByDescending { it.totalDonations })
                            }
                        }
                }
            }
            .addOnFailureListener(onError)
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