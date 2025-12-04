package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val charityName: String,
    val userEmail: String,
    val text: String,
    val timestamp: Long,
    val needsResend: Boolean
)
