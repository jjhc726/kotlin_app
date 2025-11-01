package com.example.Recyclothes.data.remote

import com.google.firebase.Timestamp

data class ScheduledDonationDto(
    val title: String,
    val dateMillis: Long,
    val timeText: String,
    val note: String?,
    val clothingType: String,
    val size: String,
    val brand: String,
    val userEmail: String,
    val createdAt: Timestamp = Timestamp.now()
)

