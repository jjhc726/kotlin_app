package com.example.Recyclothes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_charities")
data class FavoriteCharityEntity(
    @PrimaryKey val charityId: Int,
    val updatedAt: Long
)
