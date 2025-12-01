package com.example.Recyclothes.data.remote

import com.example.Recyclothes.data.local.PickupRequestEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebasePickupService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val pickups = firestore.collection("Pickups")
    suspend fun sendPickup(request: PickupRequestEntity) {
        pickups.add(request).await()
    }
}
