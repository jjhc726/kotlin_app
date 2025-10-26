package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.model.DonationItem
import com.example.vistaquickdonation.data.remote.FirebaseDonationService
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

    suspend fun getLastDonationTimestamp(userEmail: String): Timestamp? {
        return service.getLastDonationTimestamp(userEmail)
    }
}
