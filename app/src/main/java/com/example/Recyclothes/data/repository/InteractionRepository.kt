package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.InteractionService

class InteractionRepository(
    private val service: InteractionService = InteractionService()
) {

    suspend fun addCampaignInteraction() {
        service.incrementCampaignInteraction()
    }
    suspend fun addCharityInteraction() {
        service.incrementCharityInteraction()
    }
}
