package com.example.Recyclothes.data.local

import android.content.Context
import com.example.Recyclothes.data.remote.ScheduleDonationDraft
import org.json.JSONObject
import java.io.File

class ScheduleDonationDraftDisk(ctx: Context) {
    private val file = File(ctx.filesDir, "schedule_donation_drafts.json")

    fun save(key: String, draft: ScheduleDonationDraft) {
        val map = readAll().toMutableMap()
        map[key] = JSONObject().apply {
            put("title", draft.title)
            put("date", draft.date)
            put("time", draft.time)
            put("note", draft.note)
            put("clothingType", draft.clothingType)
            put("size", draft.size)
            put("brand", draft.brand)
        }
        writeAll(map)
    }

    fun load(key: String): ScheduleDonationDraft? {
        val map = readAll()
        val o = map[key] ?: return null
        return ScheduleDonationDraft(
            title = o.optString("title", ""),
            date = o.optString("date", ""),
            time = o.optString("time", ""),
            note = o.optString("note", ""),
            clothingType = o.optString("clothingType", ""),
            size = o.optString("size", ""),
            brand = o.optString("brand", "")
        )
    }

    fun clear(key: String) {
        val map = readAll().toMutableMap()
        map.remove(key)
        writeAll(map)
    }

    private fun readAll(): Map<String, JSONObject> {
        return try {
            if (!file.exists()) return emptyMap()
            val root = JSONObject(file.readText())
            buildMap {
                val it = root.keys()
                while (it.hasNext()) {
                    val k = it.next()
                    put(k, root.getJSONObject(k))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun writeAll(map: Map<String, JSONObject>) {
        val root = JSONObject()
        map.forEach { (k, v) -> root.put(k, v) }
        file.writeText(root.toString())
    }
}
