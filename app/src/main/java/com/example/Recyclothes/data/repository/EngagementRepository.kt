package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.EngagementServiceAdapter

class EngagementRepository(
    private val engagementService: EngagementServiceAdapter = EngagementServiceAdapter()
) {
    suspend fun logPickupAtHome() {
        engagementService.incrementPickupAtHome()
    }

    suspend fun logScheduleDonation() {
        engagementService.incrementScheduleDonation()
    }
}
