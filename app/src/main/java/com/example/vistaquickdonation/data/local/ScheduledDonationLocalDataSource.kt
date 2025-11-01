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


}
