package com.example.Recyclothes.data.repository

import android.content.Context
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.local.FeaturesUsageCache
import com.example.Recyclothes.data.local.FeaturesUsageDiskQueue
import com.example.Recyclothes.data.model.UsageFeedback
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Date

class FeaturesUsageRepository(ctx: Context) {

    private val col = Firebase.firestore.collection("usage_feedback")

    private val cache = FeaturesUsageCache<UsageFeedback>(maxEntries = 100)
    private val disk  = FeaturesUsageDiskQueue(ctx)
    private val net   = ConnectivityObserver(ctx)

    private suspend fun sendToFirestore(ev: UsageFeedback): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            col.add(
                mapOf(
                    "feature_name" to ev.featureName,
                    "why"          to ev.why,
                    "created_at"   to Timestamp(Date(ev.createdAt)),
                    "user_email"   to ev.userEmail
                )
            ).await()
        }.isSuccess
    }

    private fun keyFor(ev: UsageFeedback) =
        "${ev.userEmail}:${ev.featureName}:${ev.createdAt}"

    suspend fun submitFeedback(ev: UsageFeedback): Boolean = withContext(Dispatchers.IO) {
        if (net.isOnlineNow()) {
            val ok = sendToFirestore(ev)
            if (ok) return@withContext true
        }
        val k = keyFor(ev)
        cache.put(k, ev)
        disk.append(k, ev)
        false
    }

    suspend fun flushPending(): Int = withContext(Dispatchers.IO) {
        if (!net.isOnlineNow()) return@withContext 0

        val arr: JSONArray = disk.readAll()
        var sent = 0
        for (i in 0 until arr.length()) {
            val o  = arr.getJSONObject(i)
            val ev = UsageFeedback(
                userEmail   = o.getString("userEmail"),
                featureName = o.getString("featureName"),
                why         = o.getString("why"),
                createdAt   = o.getLong("createdAt")
            )
            if (sendToFirestore(ev)) sent++
        }
        if (sent == arr.length()) disk.clear()

        var sentCache = 0
        for ((k, ev) in cache.snapshot()) {
            if (sendToFirestore(ev)) {
                cache.remove(k)
                sentCache++
            }
        }
        sent + sentCache
    }
}
