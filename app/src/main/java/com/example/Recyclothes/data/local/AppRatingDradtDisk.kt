package com.example.Recyclothes.data.local

import android.content.Context
import com.example.Recyclothes.data.remote.AppRatingDraft
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AppRatingDraftDisk(ctx: Context) {
    private val file = File(ctx.filesDir, "app_rating_drafts.json")

    fun save(key: String, draft: AppRatingDraft) {
        val map = readAll().toMutableMap()
        map[key] = JSONObject().apply {
            put("stars", draft.stars)
            put("likeMost", draft.likeMost)
            put("improvements", JSONArray(draft.improvements))
            put("recommend", draft.recommend)
            put("comments", draft.comments)
        }
        writeAll(map)
    }

    fun load(key: String): AppRatingDraft? {
        val map = readAll()
        val o = map[key] ?: return null
        val improvements = buildList {
            val arr = o.optJSONArray("improvements") ?: JSONArray()
            for (i in 0 until arr.length()) add(arr.optString(i, ""))
        }
        return AppRatingDraft(
            stars = o.optInt("stars", 0),
            likeMost = o.optString("likeMost", ""),
            improvements = improvements,
            recommend = if (o.has("recommend") && !o.isNull("recommend")) o.optBoolean("recommend") else null,
            comments = o.optString("comments", "")
        )
    }

    fun clear(key: String) {
        val map = readAll().toMutableMap()
        map.remove(key)
        writeAll(map)
    }

    private fun readAll(): Map<String, JSONObject> = try {
        if (!file.exists()) emptyMap()
        else {
            val root = JSONObject(file.readText())
            buildMap {
                val it = root.keys()
                while (it.hasNext()) {
                    val k = it.next()
                    put(k, root.getJSONObject(k))
                }
            }
        }
    } catch (_: Exception) { emptyMap() }

    private fun writeAll(map: Map<String, JSONObject>) {
        val root = JSONObject()
        map.forEach { (k, v) -> root.put(k, v) }
        file.writeText(root.toString())
    }
}
