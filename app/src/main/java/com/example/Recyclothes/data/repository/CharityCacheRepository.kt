package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.toEntity
import com.example.Recyclothes.data.local.toModel
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.data.remote.CharityRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CharityCacheRepository(
    context: Context,
    private val remote: CharityRemoteDataSource = CharityRemoteDataSource()
) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.charityDao()

    fun observe(): Flow<List<Charity>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun ensureFresh(ttlMillis: Long = 60 * 60 * 1000L) {
        withContext(Dispatchers.IO) {
            val count = dao.count()
            val oldest = dao.minCachedAt()
            val now = System.currentTimeMillis()
            val needsRefresh = count == 0 || (oldest != null && now - oldest > ttlMillis)
            if (needsRefresh) {
                val remoteItems = remote.fetchAll()
                dao.clear()
                dao.upsertAll(remoteItems.map { it.toEntity(now) })
            }
        }
    }

    suspend fun refreshNow() {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val items = remote.fetchAll()
            dao.clear()
            dao.upsertAll(items.map { it.toEntity(now) })
        }
    }
}
