package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.remote.FirebaseCharitiesService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PickupRepository(
    context: Context,
    private val service: FirebaseCharitiesService = FirebaseCharitiesService()
) {

    private val db = AppDatabase.getInstance(context)
    private val pickupDao = db.pickupRequestDao()


    suspend fun sendPickupOnline(request: PickupRequestEntity) {
        val firestore = FirebaseFirestore.getInstance()
        // write the entity as a map or object; Firestore will serialize fields
        firestore.collection("Pickups").add(request).await()
    }

    suspend fun savePickupOffline(request: PickupRequestEntity) {
        pickupDao.insert(request)
    }

    suspend fun syncPendingRequests() {
        val pending = pickupDao.getPendingRequests()
        val firestore = FirebaseFirestore.getInstance()

        pending.forEach { req ->
            firestore.collection("Pickups").add(req).await()
            pickupDao.markAsSynced(req.localId)
        }
    }
}
