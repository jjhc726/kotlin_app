package com.example.Recyclothes.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "usage_drafts.db"
private const val DB_VERSION = 1

class UsageDraftsDbHelper(ctx: Context) :
    SQLiteOpenHelper(ctx.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usage_drafts(
                draftId TEXT PRIMARY KEY,
                userEmail TEXT NOT NULL,
                featureName TEXT NOT NULL,
                why TEXT NOT NULL,
                updatedAtMillis INTEGER NOT NULL,
                synced INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_usage_drafts_updated ON usage_drafts(updatedAtMillis)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_usage_drafts_user ON usage_drafts(userEmail)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}
