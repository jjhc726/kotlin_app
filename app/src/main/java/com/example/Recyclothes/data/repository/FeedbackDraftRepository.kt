package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.data.remote.FeedbackDraft
import com.example.Recyclothes.data.remote.FeedbackDraftCache
import com.example.Recyclothes.data.remote.FeedbackDraftDisk

class FeedbackDraftRepository(ctx: Context) {
    private val cache = FeedbackDraftCache()
    private val disk  = FeedbackDraftDisk(ctx)

    fun save(key: String, draft: FeedbackDraft) {
        cache.put(key, draft)
        disk.save(key, draft)
    }

    fun load(key: String): FeedbackDraft? =
        cache.get(key) ?: disk.load(key)?.also { cache.put(key, it) }

    fun clear(key: String) {
        cache.remove(key)
        disk.clear(key)
    }
}
