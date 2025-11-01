package com.example.vistaquickdonation.data.repository

import android.content.Context
import com.example.vistaquickdonation.data.local.AppDatabase
import com.example.vistaquickdonation.data.local.DonationEntity
import com.example.vistaquickdonation.data.model.DonationItem
import com.example.vistaquickdonation.data.remote.FirebaseDonationService
import com.example.vistaquickdonation.viewmodel.TopDonor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DonationRepository(
    context: Context,
    private val service: FirebaseDonationService = FirebaseDonationService()
) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.donationDao()

    suspend fun uploadDonation(item: DonationItem, userEmail: String): Boolean {
        return try {
            val success = service.uploadDonation(item, userEmail)
            if (!success) savePending(item, userEmail)
            success
        } catch (_: Exception) {
            savePending(item, userEmail)
            false
        }
    }

    private suspend fun savePending(item: DonationItem, userEmail: String) {
        withContext(Dispatchers.IO) {
            dao.insertDonation(
                DonationEntity(
                    description = item.description,
                    clothingType = item.clothingType,
                    size = item.size,
                    brand = item.brand,
                    tags = item.tags.joinToString(","),
                    userEmail = userEmail
                )
            )
        }
    }

    suspend fun syncPendingDonations() {
        val pending = dao.getAll()
        for (entity in pending) {
            val item = DonationItem(
                description = entity.description,
                clothingType = entity.clothingType,
                size = entity.size,
                brand = entity.brand,
                tags = entity.tags.split(","),
                userEmail = entity.userEmail
            )
            val uploaded = service.uploadDonation(item, entity.userEmail)
            if (uploaded) dao.deleteDonation(entity)
        }
    }

    fun listenRecentDonations(
        userEmail: String,
        limit: Int = 5,
        onChange: (List<DonationItem>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        return service.listenRecentDonations(userEmail, limit, onChange, onError)
    }

    fun getTopDonors(
        limit: Int = 5,
        onSuccess: (List<TopDonor>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            service.getTopDonors(limit, onSuccess, onError)
        } catch (e: Exception) {
            onError(e)
        }
    }

    suspend fun getLastDonationTimestamp(userEmail: String): Timestamp? {
        return service.getLastDonationTimestamp(userEmail)
    }
}
