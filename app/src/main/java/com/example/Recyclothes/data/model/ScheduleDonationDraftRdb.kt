package com.example.Recyclothes.data.model

data class ScheduleDonationDraftRdb(
    val draftId: String,
    val userEmail: String,
    val title: String,
    val date: String,
    val time: String,
    val note: String?,
    val clothingType: String,
    val size: String,
    val brand: String,
    val updatedAtMillis: Long,
    val synced: Boolean
)
