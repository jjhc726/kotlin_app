package com.example.Recyclothes.data.remote

import android.content.Context
import org.json.JSONObject
import java.io.File

class FeedbackDraftDisk(ctx: Context) {
    private val file = File(ctx.filesDir, "feedback_drafts.json")

    fun save(key: String, draft: FeedbackDraft) {
        val root = readAll()
        root.put(key, JSONObject().apply {
            put("featureName", draft.featureName)
            put("why", draft.why)
        })
        file.writeText(root.toString())
    }

    fun load(key: String): FeedbackDraft? {
        val root = readAll()
        val o = root.optJSONObject(key) ?: return null
        return FeedbackDraft(
            featureName = o.optString("featureName",""),
            why = o.optString("why","")
        )
    }

    fun clear(key: String) {
        val root = readAll()
        root.remove(key)
        file.writeText(root.toString())
    }

    private fun readAll(): JSONObject =
        try { if (file.exists()) JSONObject(file.readText()) else JSONObject() }
        catch (_: Exception) { JSONObject() }
}
