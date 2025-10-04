package com.example.vistaquickdonation.model

import com.google.firebase.Timestamp

data class DonationItem(
    val description: String = "",
    val clothingType: String = "",
    val size: String = "",
    val brand: String = "",
    val userEmail: String = "",
    val createdAt: Timestamp? = null
)
