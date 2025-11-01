package com.example.Recyclothes.data.local.model

data class OfflineScheduledDonation(
    val requestId: String,
    val userEmail: String,
    val title: String,
    val dateMillis: Long,
    val timeText: String,
    val note: String?,
    val clothingType: String,
    val size: String,
    val brand: String,
    val createdAtMillis: Long
)
