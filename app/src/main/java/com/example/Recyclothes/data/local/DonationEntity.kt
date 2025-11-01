package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val clothingType: String,
    val size: String,
    val brand: String,
    val tags: String,
    val userEmail: String
)
