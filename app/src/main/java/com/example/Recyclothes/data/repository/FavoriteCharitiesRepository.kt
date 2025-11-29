package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.FavoriteCharityEntity
import com.example.Recyclothes.data.local.FavoriteOpEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteCharitiesRepository(
    context: Context,
) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.favoriteCharityDao()

    fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeFavorites().map { list -> list.map { it.charityId }.toSet() }

    suspend fun toggleFavorite(id: Int) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (dao.isFavorite(id)) {
            dao.deleteFavorite(id)
            dao.enqueueOp(FavoriteOpEntity(charityId = id, op = "REMOVE", enqueuedAt = now))
        } else {
            dao.upsertFavorite(FavoriteCharityEntity(charityId = id, updatedAt = now))
            dao.enqueueOp(FavoriteOpEntity(charityId = id, op = "ADD", enqueuedAt = now))
        }
    }


}
