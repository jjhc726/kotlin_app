package com.example.Recyclothes.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.Recyclothes.data.local.DraftDonationEntity

@Dao
interface DraftDao {
    @Insert
    suspend fun insertDraft(draft: DraftDonationEntity): Long

    @Update
    suspend fun updateDraft(draft: DraftDonationEntity)

    @Query("SELECT * FROM draft_donations ORDER BY id DESC")
    suspend fun getAllDrafts(): List<DraftDonationEntity>

    @Query("SELECT * FROM draft_donations WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: Long): DraftDonationEntity?

    @Delete
    suspend fun deleteDraft(draft: DraftDonationEntity)
}