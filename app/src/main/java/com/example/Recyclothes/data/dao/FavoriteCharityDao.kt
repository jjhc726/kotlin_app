package com.example.Recyclothes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Recyclothes.data.local.FavoriteCharityEntity
import com.example.Recyclothes.data.local.FavoriteOpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCharityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(entity: FavoriteCharityEntity)

    @Query("DELETE FROM favorite_charities WHERE charityId = :id")
    suspend fun deleteFavorite(id: Int)

    @Query("SELECT * FROM favorite_charities ORDER BY updatedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteCharityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_charities WHERE charityId = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT charityId FROM favorite_charities")
    suspend fun allIds(): List<Int>

    @Insert
    suspend fun enqueueOp(op: FavoriteOpEntity)

    @Query("SELECT * FROM favorite_ops ORDER BY enqueuedAt ASC")
    suspend fun pendingOps(): List<FavoriteOpEntity>

    @Query("DELETE FROM favorite_ops WHERE id IN (:ids)")
    suspend fun deleteOps(ids: List<Long>)
}
