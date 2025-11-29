package com.example.Recyclothes.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_ops")
data class FavoriteOpEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val charityId: Int,
    val op: String,
    @ColumnInfo(name = "enqueuedAt") val enqueuedAt: Long
)
