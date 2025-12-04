package com.example.Recyclothes.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.Recyclothes.data.local.db.ScheduledDonationDbHelper
import com.example.Recyclothes.data.remote.ScheduledDonationDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class OfflineScheduledDonationRepository(ctx: Context) {

    private val db = ScheduledDonationDbHelper(ctx).writableDatabase

    fun queue(
        userEmail: String,
        title: String,
        dateMillis: Long,
        timeText: String,
        note: String?,
        clothingType: String,
        size: String,
        brand: String
    ): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val cv = ContentValues().apply {
            put("requestId", id)
            put("userEmail", userEmail)
            put("title", title)
            put("dateMillis", dateMillis)
            put("timeText", timeText)
            put("note", note)
            put("clothingType", clothingType)
            put("size", size)
            put("brand", brand)
            put("createdAtMillis", now)
        }
        db.insert("offline_scheduled", null, cv)
        return id
    }

    private data class Row(val requestId: String, val dto: ScheduledDonationDto)

    private fun Cursor.getStringOrNull(name: String): String? {
        val idx = getColumnIndex(name)
        return if (idx >= 0 && !isNull(idx)) getString(idx) else null
    }

    private companion object {
        private const val BATCH_SIZE = 25
    }

    private fun readBatch(): List<Row> {
        val rows = mutableListOf<Row>()
        val c = db.query(
            "offline_scheduled",
            arrayOf(
                "requestId", "userEmail", "title", "dateMillis",
                "timeText", "note", "clothingType", "size", "brand"
            ),
            null,
            null,
            null,
            null,
            "createdAtMillis ASC",
            BATCH_SIZE.toString()
        )
        c.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow("requestId"))
                val dto = ScheduledDonationDto(
                    title        = it.getString(it.getColumnIndexOrThrow("title")),
                    dateMillis   = it.getLong(it.getColumnIndexOrThrow("dateMillis")),
                    timeText     = it.getString(it.getColumnIndexOrThrow("timeText")),
                    note         = it.getStringOrNull("note"),
                    clothingType = it.getString(it.getColumnIndexOrThrow("clothingType")),
                    size         = it.getString(it.getColumnIndexOrThrow("size")),
                    brand        = it.getString(it.getColumnIndexOrThrow("brand")),
                    userEmail    = it.getString(it.getColumnIndexOrThrow("userEmail"))
                )
                rows += Row(id, dto)
            }
        }
        return rows
    }

    private fun deleteById(requestId: String) {
        db.delete("offline_scheduled", "requestId = ?", arrayOf(requestId))
    }

    suspend fun flushPending(onlineRepo: ScheduleDonationRepository): Int = withContext(Dispatchers.IO) {
        var sent = 0
        while (true) {
            val batch = readBatch()
            if (batch.isEmpty()) break
            for (row in batch) {
                val ok = onlineRepo.create(row.dto)
                if (ok) {
                    deleteById(row.requestId)
                    sent++
                } else {
                    return@withContext sent
                }
            }
        }
        sent
    }
}
