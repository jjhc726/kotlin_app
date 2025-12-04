package com.example.Recyclothes.data.model

data class Message(
    val id: String = "",
    val fromUserId: String = "",
    val toCharityId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
