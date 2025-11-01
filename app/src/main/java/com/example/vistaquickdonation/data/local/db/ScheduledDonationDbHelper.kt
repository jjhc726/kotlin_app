package com.example.vistaquickdonation.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "offline_queue.db"
private const val DB_VERSION = 2

class ScheduledDonationDbHelper(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS offline_scheduled (
                requestId TEXT PRIMARY KEY,
                userEmail TEXT NOT NULL,
                title TEXT NOT NULL,
                dateMillis INTEGER NOT NULL,
                timeText TEXT NOT NULL,
                note TEXT,
                clothingType TEXT NOT NULL,
                size TEXT NOT NULL,
                brand TEXT NOT NULL,
                createdAtMillis INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sched_created ON offline_scheduled(createdAtMillis)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS offline_scheduled")
        onCreate(db)
    }
}
