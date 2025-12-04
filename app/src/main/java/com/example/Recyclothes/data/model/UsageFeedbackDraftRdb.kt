package com.example.Recyclothes.data.model

data class UsageFeedbackDraftRdb(
    val draftId: String,
    val userEmail: String,
    val featureName: String,
    val why: String,
    val updatedAtMillis: Long,
    val synced: Boolean
)
