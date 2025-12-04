package com.example.Recyclothes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasonal_campaigns")
data class SeasonalCampaignEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val imageRes: Int // ID del recurso drawable
)
