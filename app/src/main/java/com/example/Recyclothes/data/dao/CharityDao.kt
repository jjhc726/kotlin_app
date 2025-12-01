package com.example.Recyclothes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Recyclothes.data.local.CharityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharityDao {

    @Query("SELECT * FROM charities ORDER BY name")
    fun observeAll(): Flow<List<CharityEntity>>

    @Query("SELECT COUNT(*) FROM charities")
    suspend fun count(): Int

    @Query("SELECT MIN(cachedAt) FROM charities")
    suspend fun minCachedAt(): Long?

    @Query("SELECT MAX(cachedAt) FROM charities")
    suspend fun lastCacheTs(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CharityEntity>)

    @Query("DELETE FROM charities")
    suspend fun clear()

    @Query("SELECT * FROM charities WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CharityEntity?

    @Query("SELECT * FROM charities WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<CharityEntity>
}