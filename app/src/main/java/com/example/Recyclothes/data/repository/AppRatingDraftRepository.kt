package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.AppRatingDraftCache
import com.example.Recyclothes.data.local.AppRatingDraftDisk
import com.example.Recyclothes.data.remote.AppRatingDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRatingDraftRepository(ctx: Context) {
    private val lru  = AppRatingDraftCache(20)
    private val disk = AppRatingDraftDisk(ctx)

    suspend fun save(key: String, draft: AppRatingDraft) = withContext(Dispatchers.IO) {
        lru.put(key, draft)
        disk.save(key, draft)
    }

    suspend fun load(key: String): AppRatingDraft? = withContext(Dispatchers.IO) {
        lru.get(key) ?: disk.load(key)?.also { lru.put(key, it) }
    }

    suspend fun clear(key: String) = withContext(Dispatchers.IO) {
        lru.remove(key)
        disk.clear(key)
    }
}
