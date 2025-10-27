package com.example.vistaquickdonation.data.repository

import com.example.vistaquickdonation.data.model.DonationPoint
import com.example.vistaquickdonation.data.remote.FirebaseCharitiesService


class CharitiesRepository(
    private val service: FirebaseCharitiesService = FirebaseCharitiesService()
) {

    suspend fun getAllDonationPoints(): List<DonationPoint> {
        return service.getAllDonationPoints()
    }

}
