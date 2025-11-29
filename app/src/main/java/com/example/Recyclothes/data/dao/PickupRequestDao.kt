package com.example.Recyclothes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Recyclothes.data.local.PickupRequestEntity

@Dao
interface PickupRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: PickupRequestEntity)

    @Query("SELECT * FROM pickup_requests WHERE synced = 0")
    suspend fun getPendingRequests(): List<PickupRequestEntity>

    @Query("UPDATE pickup_requests SET synced = 1 WHERE localId = :id")
    suspend fun markAsSynced(id: Int)
}
