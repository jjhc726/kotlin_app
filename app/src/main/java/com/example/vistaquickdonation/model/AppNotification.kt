package com.example.vistaquickdonation.model

import java.util.Date

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: Date? = null,
    val read: Boolean = false
)
