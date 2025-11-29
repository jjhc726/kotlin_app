package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_donations")
data class DraftDonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val clothingType: String,
    val size: String,
    val brand: String,
    val tags: String,
    val userEmail: String
)
