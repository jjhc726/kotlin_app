package com.example.Recyclothes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Recyclothes.data.local.entity.SeasonalCampaignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonalCampaignDao {

    @Query("SELECT * FROM seasonal_campaigns")
    fun getCampaigns(): Flow<List<SeasonalCampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<SeasonalCampaignEntity>)
}
