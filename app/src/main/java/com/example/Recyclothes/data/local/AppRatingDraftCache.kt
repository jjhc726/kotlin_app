package com.example.Recyclothes.data.local

import android.util.LruCache
import com.example.Recyclothes.data.remote.AppRatingDraft

class AppRatingDraftCache(maxEntries: Int = 20) {
    private val lru = object : LruCache<String, AppRatingDraft>(maxEntries) {
        override fun sizeOf(key: String, value: AppRatingDraft) = 1
    }
    fun get(key: String): AppRatingDraft? = lru.get(key)
    fun put(key: String, draft: AppRatingDraft) { lru.put(key, draft) }
    fun remove(key: String) { lru.remove(key) }
    fun clear() { lru.evictAll() }
}
