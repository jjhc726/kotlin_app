package com.example.Recyclothes.data.remote

import android.util.LruCache

class FeedbackDraftCache(maxEntries: Int = 30) :
    LruCache<String, FeedbackDraft>(maxEntries) {
    override fun sizeOf(key: String, value: FeedbackDraft) = 1
}