package com.example.Recyclothes.data.local

import android.util.LruCache

class FeaturesUsageCache<T>(maxEntries: Int = 100) {
    private val cache = object : LruCache<String, T>(maxEntries) {
        override fun sizeOf(key: String, value: T) = 1
    }
    fun put(key: String, value: T)            { cache.put(key, value) }
    fun snapshot(): Map<String, T>            = cache.snapshot()
    fun remove(key: String)                   { cache.remove(key) }
}
