package com.example.vistaquickdonation.data.model

data class Charity(
    val id: Int,
    val name: String,
    val description: String,
    val campaigns: List<String>
)