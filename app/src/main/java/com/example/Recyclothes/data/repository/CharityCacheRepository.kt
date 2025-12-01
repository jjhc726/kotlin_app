package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.dao.CharityDao
import com.example.Recyclothes.data.local.AppDatabase
import com.example.Recyclothes.data.local.toEntity
import com.example.Recyclothes.data.local.toModel
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.data.remote.CharityRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours

class CharityCacheRepository(
    context: Context,
    private val remote: CharityRemoteDataSource = CharityRemoteDataSource()
) {
    private val dao: CharityDao = AppDatabase.getInstance(context).charityDao()
    private val TTL_MS = 24.hours.inWholeMilliseconds

    fun observe(): Flow<List<Charity>> =
        dao.observeAll().map { it.map { e -> e.toModel() } }

    suspend fun ensureFresh() = withContext(Dispatchers.IO) {
        val last = dao.lastCacheTs() ?: 0L
        val now = System.currentTimeMillis()
        if (now - last < TTL_MS && dao.count() > 0) return@withContext

        val remoteList = remote.fetchAll()
        val entities = remoteList.map { it.toEntity(now) }
        dao.clear()
        dao.upsertAll(entities)
    }

    suspend fun refreshNow() = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val remoteList = remote.fetchAll()
        val entities = remoteList.map { it.toEntity(now) }
        dao.clear()
        dao.upsertAll(entities)
    }
}
