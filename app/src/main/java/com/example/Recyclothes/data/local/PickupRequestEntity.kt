package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pickup_requests")
data class PickupRequestEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val userId: String,
    val donationId: String,
    val address: String,
    val date: String,
    val hour: String,
    val synced: Boolean = false,
    val isCompleted: Boolean = false
)