package com.example.Recyclothes.viewmodel

import com.example.Recyclothes.data.model.FeatureId

data class LeastUsedUi(
    val featureId: FeatureId,
    val label: String,
    val weeklyCount: Int
)
