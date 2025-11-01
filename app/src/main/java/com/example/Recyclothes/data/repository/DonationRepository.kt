package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.model.DonationItem
import com.example.Recyclothes.data.remote.FirebaseDonationService
import com.example.Recyclothes.viewmodel.TopDonor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration

class DonationRepository(
    private val service: FirebaseDonationService = FirebaseDonationService()
) {
    suspend fun uploadDonation(item: DonationItem, userEmail: String): Boolean {
        return service.uploadDonation(item, userEmail)
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
