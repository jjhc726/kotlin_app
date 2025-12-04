package com.example.Recyclothes.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.Recyclothes.data.local.db.ScheduleDraftsDbHelper
import com.example.Recyclothes.data.model.ScheduleDonationDraftRdb
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class ScheduleDonationDraftsRdbRepository(ctx: Context) {

    private val db = ScheduleDraftsDbHelper(ctx).writableDatabase
    private val draftsCol = Firebase.firestore.collection("drafts_schedule")

    fun upsert(draft: ScheduleDonationDraftRdb): String {
        val cv = ContentValues().apply {
            put("draftId", draft.draftId)
            put("userEmail", draft.userEmail)
            put("title", draft.title)
            put("date", draft.date)
            put("time", draft.time)
            put("note", draft.note)
            put("clothingType", draft.clothingType)
            put("size", draft.size)
            put("brand", draft.brand)
            put("updatedAtMillis", draft.updatedAtMillis)
            put("synced", if (draft.synced) 1 else 0)
        }
        db.insertWithOnConflict("schedule_drafts", null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
        return draft.draftId
    }

    fun createOrUpdate(
        userEmail: String,
        title: String,
        date: String,
        time: String,
        note: String?,
        clothingType: String,
        size: String,
        brand: String,
        existingId: String? = null
    ): String {
        val id = existingId ?: UUID.randomUUID().toString()
        upsert(
            ScheduleDonationDraftRdb(
                draftId = id,
                userEmail = userEmail,
                title = title,
                date = date,
                time = time,
                note = note,
                clothingType = clothingType,
                size = size,
                brand = brand,
                updatedAtMillis = System.currentTimeMillis(),
                synced = false
            )
        )
        return id
    }

    private fun Cursor.str(col: String) = getString(getColumnIndexOrThrow(col))
    private fun Cursor.strOrNull(col: String): String? {
        val idx = getColumnIndex(col)
        return if (idx >= 0 && !isNull(idx)) getString(idx) else null
    }

    fun listAll(userEmail: String): List<ScheduleDonationDraftRdb> {
        val items = mutableListOf<ScheduleDonationDraftRdb>()
        val c = db.query(
            "schedule_drafts",
            null,
            "userEmail = ?",
            arrayOf(userEmail),
            null, null,
            "updatedAtMillis DESC"
        )
        c.use {
            while (it.moveToNext()) {
                items += ScheduleDonationDraftRdb(
                    draftId = it.str("draftId"),
                    userEmail = it.str("userEmail"),
                    title = it.str("title"),
                    date = it.str("date"),
                    time = it.str("time"),
                    note = it.strOrNull("note"),
                    clothingType = it.str("clothingType"),
                    size = it.str("size"),
                    brand = it.str("brand"),
                    updatedAtMillis = it.getLong(it.getColumnIndexOrThrow("updatedAtMillis")),
                    synced = it.getInt(it.getColumnIndexOrThrow("synced")) == 1
                )
            }
        }
        return items
    }

    fun getById(draftId: String): ScheduleDonationDraftRdb? {
        val c = db.query("schedule_drafts", null, "draftId = ?", arrayOf(draftId), null, null, null, "1")
        c.use {
            if (!it.moveToFirst()) return null
            return ScheduleDonationDraftRdb(
                draftId = it.str("draftId"),
                userEmail = it.str("userEmail"),
                title = it.str("title"),
                date = it.str("date"),
                time = it.str("time"),
                note = it.strOrNull("note"),
                clothingType = it.str("clothingType"),
                size = it.str("size"),
                brand = it.str("brand"),
                updatedAtMillis = it.getLong(it.getColumnIndexOrThrow("updatedAtMillis")),
                synced = it.getInt(it.getColumnIndexOrThrow("synced")) == 1
            )
        }
    }

    fun deleteById(draftId: String) {
        db.delete("schedule_drafts", "draftId = ?", arrayOf(draftId))
    }

    private fun markSynced(draftId: String) {
        val cv = ContentValues().apply { put("synced", 1) }
        db.update("schedule_drafts", cv, "draftId = ?", arrayOf(draftId))
    }

    suspend fun flushPendingToFirebase(): Int = withContext(Dispatchers.IO) {
        var sent = 0
        val c = db.query("schedule_drafts", null, "synced = 0", null, null, null, "updatedAtMillis ASC")
        c.use {
            while (it.moveToNext()) {
                val d = ScheduleDonationDraftRdb(
                    draftId = it.getString(it.getColumnIndexOrThrow("draftId")),
                    userEmail = it.getString(it.getColumnIndexOrThrow("userEmail")),
                    title = it.getString(it.getColumnIndexOrThrow("title")),
                    date = it.getString(it.getColumnIndexOrThrow("date")),
                    time = it.getString(it.getColumnIndexOrThrow("time")),
                    note = it.strOrNull("note"),
                    clothingType = it.getString(it.getColumnIndexOrThrow("clothingType")),
                    size = it.getString(it.getColumnIndexOrThrow("size")),
                    brand = it.getString(it.getColumnIndexOrThrow("brand")),
                    updatedAtMillis = it.getLong(it.getColumnIndexOrThrow("updatedAtMillis")),
                    synced = false
                )
                val payload = mapOf(
                    "userEmail" to d.userEmail,
                    "title" to d.title,
                    "date" to d.date,
                    "time" to d.time,
                    "note" to d.note,
                    "clothingType" to d.clothingType,
                    "size" to d.size,
                    "brand" to d.brand,
                    "updatedAtMillis" to d.updatedAtMillis
                )
                val ok = runCatching {
                    draftsCol.document(d.draftId).set(payload, SetOptions.merge()).await()
                }.isSuccess
                if (ok) { markSynced(d.draftId); sent++ } else break
            }
        }
        sent
    }
}
