package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.data.remote.FirebasePickupService
class PickupRepository(
    context: Context,
    private val remote: FirebasePickupService = FirebasePickupService()
) {

    private val db = AppDatabase.getInstance(context)
    private val pickupDao = db.pickupRequestDao()

    suspend fun sendPickupOnline(request: PickupRequestEntity) {
        remote.sendPickup(request)
    }

    suspend fun savePickupOffline(request: PickupRequestEntity) {
        pickupDao.insert(request)
    }

    suspend fun syncPendingRequests() {
        val pending = pickupDao.getPendingRequests()

        pending.forEach { req ->
            remote.sendPickup(req)
            pickupDao.markAsSynced(req.localId)
        }
    }
    suspend fun getPendingPickups() = pickupDao.getPendingRequests()
}
