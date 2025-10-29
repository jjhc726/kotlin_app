package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.remote.InteractionService

class InteractionRepository(
    private val service: InteractionService = InteractionService()
) {

    suspend fun addInteraction() {
        service.incrementInteractions()
    }

    suspend fun fetchTotalInteractions(): Long {
        return service.getInteractions()
    }
}
