package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.remote.InteractionService

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
