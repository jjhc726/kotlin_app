package com.example.Recyclothes.data.local

import android.content.Context
import com.example.Recyclothes.data.model.UsageFeedback
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FeaturesUsageDiskQueue(ctx: Context) {
    private val file = File(ctx.filesDir, "features_usage_queue.json")

    fun append(key: String, ev: UsageFeedback) {
        val arr = readAll()
        arr.put(JSONObject().apply {
            put("key", key)
            put("userEmail", ev.userEmail)
            put("featureName", ev.featureName)
            put("why", ev.why)
            put("createdAt", ev.createdAt)
        })
        file.writeText(arr.toString())
    }

    fun readAll(): JSONArray = try {
        if (file.exists()) JSONArray(file.readText()) else JSONArray()
    } catch (_: Exception) { JSONArray() }

    fun clear() { if (file.exists()) file.writeText("[]") }
}
