package com.example.Recyclothes.data.local

import android.util.LruCache
import com.example.Recyclothes.data.remote.ScheduleDonationDraft

class ScheduleDonationDraftCache(maxEntries: Int = 20) {
    private val lru = object : LruCache<String, ScheduleDonationDraft>(maxEntries) {
        override fun sizeOf(key: String, value: ScheduleDonationDraft) = 1
    }
    fun get(key: String): ScheduleDonationDraft? = lru.get(key)
    fun put(key: String, draft: ScheduleDonationDraft) { lru.put(key, draft) }
    fun remove(key: String) { lru.remove(key) }
    fun clear() { lru.evictAll() }
}
