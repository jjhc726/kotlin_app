package com.example.vistaquickdonation.data.local

import android.content.ContentValues
import android.content.Context
import com.example.vistaquickdonation.data.local.db.ScheduledDonationDbHelper
import com.example.vistaquickdonation.data.local.model.OfflineScheduledDonation

class ScheduledDonationLocalDataSource(context: Context) {

    private val helper = ScheduledDonationDbHelper(context)

    fun upsert(e: OfflineScheduledDonation) {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put("requestId", e.requestId)
            put("userEmail", e.userEmail)
            put("title", e.title)
            put("dateMillis", e.dateMillis)
            put("timeText", e.timeText)
            put("note", e.note)
            put("clothingType", e.clothingType)
            put("size", e.size)
            put("brand", e.brand)
            put("createdAtMillis", e.createdAtMillis)
        }
        db.insertWithOnConflict("offline_scheduled", null, v,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAll(): List<OfflineScheduledDonation> {
        val db = helper.readableDatabase
        val list = mutableListOf<OfflineScheduledDonation>()
        db.rawQuery("""
            SELECT requestId,userEmail,title,dateMillis,timeText,note,clothingType,size,brand,createdAtMillis
            FROM offline_scheduled ORDER BY createdAtMillis ASC
        """.trimIndent(), null).use { c ->
            while (c.moveToNext()) {
                list += OfflineScheduledDonation(
                    requestId = c.getString(0),
                    userEmail = c.getString(1),
                    title = c.getString(2),
                    dateMillis = c.getLong(3),
                    timeText = c.getString(4),
                    note = c.getString(5),
                    clothingType = c.getString(6),
                    size = c.getString(7),
                    brand = c.getString(8),
                    createdAtMillis = c.getLong(9)
                )
            }
        }
        return list
    }

    fun deleteById(id: String) {
        helper.writableDatabase.delete("offline_scheduled", "requestId=?", arrayOf(id))
    }
}
