package com.example.Recyclothes.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "usage_feedback_offline.db"
private const val DB_VERSION = 1

class UsageFeedbackDbHelper(ctx: Context) :
    SQLiteOpenHelper(ctx.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS offline_usage_feedback(
                requestId TEXT PRIMARY KEY,
                userEmail TEXT,
                featureName TEXT NOT NULL,
                why TEXT NOT NULL,
                createdAtMillis INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_uff_created ON offline_usage_feedback(createdAtMillis)")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS offline_usage_feedback")
        onCreate(db)
    }
}
