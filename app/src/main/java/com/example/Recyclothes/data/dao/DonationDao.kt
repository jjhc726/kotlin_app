package com.example.Recyclothes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.Recyclothes.data.local.DonationEntity

@Dao
interface DonationDao {
    @Insert
    suspend fun insertDonation(donation: DonationEntity)

    @Query("SELECT * FROM pending_donations")
    suspend fun getAll(): List<DonationEntity>

    @Delete
    suspend fun deleteDonation(donation: DonationEntity)

    @Query("DELETE FROM pending_donations")
    suspend fun clearAll()
}