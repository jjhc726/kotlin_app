package com.example.Recyclothes.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "schedule_drafts.db"
private const val DB_VERSION = 1

class ScheduleDraftsDbHelper(ctx: Context) :
    SQLiteOpenHelper(ctx.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS schedule_drafts(
                draftId TEXT PRIMARY KEY,
                userEmail TEXT NOT NULL,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                note TEXT,
                clothingType TEXT NOT NULL,
                size TEXT NOT NULL,
                brand TEXT NOT NULL,
                updatedAtMillis INTEGER NOT NULL,
                synced INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sched_drafts_updated ON schedule_drafts(updatedAtMillis)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sched_drafts_user ON schedule_drafts(userEmail)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
