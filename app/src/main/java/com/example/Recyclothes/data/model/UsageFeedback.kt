package com.example.Recyclothes.data.model

data class UsageFeedback (
    val userEmail: String,
    val featureName: String,
    val why: String,
    val createdAt: Long
)