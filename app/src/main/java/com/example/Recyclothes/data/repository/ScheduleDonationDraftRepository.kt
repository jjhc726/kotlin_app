package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.local.ScheduleDonationDraftCache
import com.example.Recyclothes.data.local.ScheduleDonationDraftDisk
import com.example.Recyclothes.data.remote.ScheduleDonationDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleDonationDraftRepository(ctx: Context) {
    private val lru  = ScheduleDonationDraftCache(20)
    private val disk = ScheduleDonationDraftDisk(ctx)

    suspend fun save(key: String, draft: ScheduleDonationDraft) = withContext(Dispatchers.IO) {
        lru.put(key, draft)
        disk.save(key, draft)
    }

    suspend fun load(key: String): ScheduleDonationDraft? = withContext(Dispatchers.IO) {
        lru.get(key) ?: disk.load(key)?.also { lru.put(key, it) }
    }

    suspend fun clear(key: String) = withContext(Dispatchers.IO) {
        lru.remove(key)
        disk.clear(key)
    }
}
