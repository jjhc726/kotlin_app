package com.example.Recyclothes.data.local

import android.content.Context
import com.example.Recyclothes.data.remote.ScheduleDonationDraft
import org.json.JSONObject
import java.io.File

class ScheduleDonationDraftDisk(ctx: Context) {
    private val file = File(ctx.filesDir, "schedule_donation_drafts.json")

    private fun readJson(): JSONObject =
        try { if (file.exists()) JSONObject(file.readText()) else JSONObject() }
        catch (_: Exception) { JSONObject() }

    private fun writeJson(obj: JSONObject) { file.writeText(obj.toString()) }

    fun load(key: String): ScheduleDonationDraft? {
        val root = readJson()
        if (!root.has(key)) return null
        val o = root.getJSONObject(key)
        return ScheduleDonationDraft(
            title = o.optString("title"),
            date = o.optString("date"),
            time = o.optString("time"),
            note = o.optString("note"),
            clothingType = o.optString("clothingType"),
            size = o.optString("size"),
            brand = o.optString("brand")
        )
    }

    fun save(key: String, d: ScheduleDonationDraft) {
        val root = readJson()
        val o = JSONObject()
            .put("title", d.title)
            .put("date", d.date)
            .put("time", d.time)
            .put("note", d.note)
            .put("clothingType", d.clothingType)
            .put("size", d.size)
            .put("brand", d.brand)
        root.put(key, o)
        writeJson(root)
    }

    fun clear(key: String) {
        val root = readJson()
        root.remove(key)
        writeJson(root)
    }
}
