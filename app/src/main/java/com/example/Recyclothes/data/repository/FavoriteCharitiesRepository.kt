package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.FavoriteCharityEntity
import com.example.Recyclothes.data.local.FavoriteOpEntity
import com.example.Recyclothes.data.remote.FavoriteRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteCharitiesRepository(
    context: Context,
    private val remote: FavoriteRemoteDataSource = FavoriteRemoteDataSource()
) {
    private val db = AppDatabase.getInstance(context)
    private val favDao = db.favoriteCharityDao()
    private val charityDao = db.charityDao()

    fun observeFavoriteIds(): Flow<Set<Int>> =
        favDao.observeFavorites().map { list -> list.map(FavoriteCharityEntity::charityId).toSet() }

    suspend fun getFavoriteCharities(ids: Set<Int>) = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) emptyList()
        else charityDao.getByIds(ids.toList())
    }



    suspend fun toggleFavorite(id: Int) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (favDao.isFavorite(id)) {
            favDao.deleteFavorite(id)
            favDao.enqueueOp(FavoriteOpEntity(charityId = id, op = "REMOVE", enqueuedAt = now))
        } else {
            favDao.upsertFavorite(FavoriteCharityEntity(charityId = id, updatedAt = now))
            favDao.enqueueOp(FavoriteOpEntity(charityId = id, op = "ADD", enqueuedAt = now))
        }
    }



    suspend fun top3(): List<Pair<Int, Long>> = withContext(Dispatchers.IO) { remote.top3() }
}
