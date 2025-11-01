package com.example.vistaquickdonation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vistaquickdonation.data.local.DonationPointEntity

@Dao
interface DonationPointDao {
    @Query("SELECT * FROM donation_points")
    suspend fun getAll(): List<DonationPointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<DonationPointEntity>)

    @Query("DELETE FROM donation_points")
    suspend fun clearAll()
}