package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.Recyclothes.data.model.Charity

@Entity(tableName = "charities")
data class CharityEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val campaignsCsv: String,
    val cachedAt: Long
)

fun CharityEntity.toModel(): Charity =
    Charity(
        id = id,
        name = name,
        description = description,
        campaigns = if (campaignsCsv.isBlank()) emptyList()
        else campaignsCsv.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    )

fun Charity.toEntity(now: Long): CharityEntity =
    CharityEntity(
        id = id,
        name = name,
        description = description,
        campaignsCsv = campaigns.joinToString("|"),
        cachedAt = now
    )