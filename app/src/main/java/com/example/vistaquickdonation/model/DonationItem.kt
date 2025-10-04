package com.example.vistaquickdonation.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class DonationItem(
    val description: String = "",
    val clothingType: String = "",
    val size: String = "",
    val brand: String = "",
    @ServerTimestamp val createdAt: Date? = null
)
