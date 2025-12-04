package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.DonationEntity
import com.example.Recyclothes.data.local.DraftDonationEntity
import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.data.remote.FirebaseDonationService
import com.example.Recyclothes.viewmodel.TopDonor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DonationRepository(
    context: Context,
    private val service: FirebaseDonationService = FirebaseDonationService()
) {

    private val db = AppDatabase.getInstance(context)

    private val pendingDao = db.donationDao()

    private val draftDao = db.draftDao()

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
            pendingDao.insertDonation(
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
        val pending = pendingDao.getAll()
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
            if (uploaded) pendingDao.deleteDonation(entity)
        }
    }

    suspend fun getUserDonations(userEmail: String): List<DonationItem> {
        return try {
            service.getDonationsByUser(userEmail)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // -------------------------------
    // FIREBASE QUERIES
    // -------------------------------

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



    // -------------------------------
    // BORRADORES (drafts)
    // -------------------------------

    suspend fun insertDraft(entity: DraftDonationEntity): Long {
        return draftDao.insertDraft(entity)
    }

    suspend fun updateDraft(entity: DraftDonationEntity) {
        draftDao.updateDraft(entity)
    }

    suspend fun getAllDrafts(): List<DraftDonationEntity> {
        return draftDao.getAllDrafts()
    }

    suspend fun getDraftById(id: Long): DraftDonationEntity? {
        return draftDao.getDraftById(id)
    }

    suspend fun insertPending(donation: DonationEntity) {
        pendingDao.insertPendingDonation(donation)
    }
}