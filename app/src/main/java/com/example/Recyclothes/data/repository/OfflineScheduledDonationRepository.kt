package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.ScheduledDonationLocalDataSource
import com.example.Recyclothes.data.local.model.OfflineScheduledDonation
import java.util.UUID

class OfflineScheduledDonationRepository(context: Context) {

    private val local = ScheduledDonationLocalDataSource(context)

    fun queue(
        userEmail: String,
        title: String,
        dateMillis: Long,
        timeText: String,
        note: String?,
        clothingType: String,
        size: String,
        brand: String
    ): OfflineScheduledDonation {
        val entity = OfflineScheduledDonation(
            requestId = UUID.randomUUID().toString(),
            userEmail = userEmail.trim().lowercase(),
            title = title,
            dateMillis = dateMillis,
            timeText = timeText,
            note = note,
            clothingType = clothingType,
            size = size,
            brand = brand,
            createdAtMillis = System.currentTimeMillis()
        )
        local.upsert(entity)
        return entity
    }

}
