package com.example.Recyclothes.data.model

data class Charity(
    val id: Int,
    val name: String,
    val description: String,
    val campaigns: List<String>
)