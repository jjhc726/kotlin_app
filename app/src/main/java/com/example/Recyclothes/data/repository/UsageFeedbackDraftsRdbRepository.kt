package com.example.Recyclothes.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.Recyclothes.data.local.db.UsageDraftsDbHelper
import com.example.Recyclothes.data.model.UsageFeedbackDraftRdb
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class UsageFeedbackDraftsRdbRepository(ctx: Context) {

    private val db = UsageDraftsDbHelper(ctx).writableDatabase
    private val draftsCol = Firebase.firestore.collection("drafts_usage")

    fun upsert(draft: UsageFeedbackDraftRdb): String {
        val cv = ContentValues().apply {
            put("draftId", draft.draftId)
            put("userEmail", draft.userEmail)
            put("featureName", draft.featureName)
            put("why", draft.why)
            put("updatedAtMillis", draft.updatedAtMillis)
            put("synced", if (draft.synced) 1 else 0)
        }
        db.insertWithOnConflict("usage_drafts", null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
        return draft.draftId
    }

    fun createOrUpdate(
        userEmail: String,
        featureName: String,
        why: String,
        existingId: String? = null
    ): String {
        val id = existingId ?: UUID.randomUUID().toString()
        upsert(
            UsageFeedbackDraftRdb(
                draftId = id,
                userEmail = userEmail,
                featureName = featureName,
                why = why,
                updatedAtMillis = System.currentTimeMillis(),
                synced = false
            )
        )
        return id
    }

    private fun Cursor.str(col: String) = getString(getColumnIndexOrThrow(col))

    fun listAll(userEmail: String): List<UsageFeedbackDraftRdb> {
        val items = mutableListOf<UsageFeedbackDraftRdb>()
        val c = db.query(
            "usage_drafts",
            null,
            "userEmail = ?",
            arrayOf(userEmail),
            null, null,
            "updatedAtMillis DESC"
        )
        c.use {
            while (it.moveToNext()) {
                items += UsageFeedbackDraftRdb(
                    draftId = it.str("draftId"),
                    userEmail = it.str("userEmail"),
                    featureName = it.str("featureName"),
                    why = it.str("why"),
                    updatedAtMillis = it.getLong(it.getColumnIndexOrThrow("updatedAtMillis")),
                    synced = it.getInt(it.getColumnIndexOrThrow("synced")) == 1
                )
            }
        }
        return items
    }

    fun getById(draftId: String): UsageFeedbackDraftRdb? {
        val c = db.query("usage_drafts", null, "draftId = ?", arrayOf(draftId), null, null, null, "1")
        c.use {
            if (!it.moveToFirst()) return null
            return UsageFeedbackDraftRdb(
                draftId = it.str("draftId"),
                userEmail = it.str("userEmail"),
                featureName = it.str("featureName"),
                why = it.str("why"),
                updatedAtMillis = it.getLong(it.getColumnIndexOrThrow("updatedAtMillis")),
                synced = it.getInt(it.getColumnIndexOrThrow("synced")) == 1
            )
        }
    }

    fun deleteById(draftId: String) {
        db.delete("usage_drafts", "draftId = ?", arrayOf(draftId))
    }

    private fun markSynced(draftId: String) {
        val cv = ContentValues().apply { put("synced", 1) }
        db.update("usage_drafts", cv, "draftId = ?", arrayOf(draftId))
    }

    suspend fun flushPendingToFirebase(): Int = withContext(Dispatchers.IO) {
        var sent = 0
        val c = db.query("usage_drafts", null, "synced = 0", null, null, null, "updatedAtMillis ASC")
        c.use {
            while (it.moveToNext()) {
                val payload = mapOf(
                    "userEmail" to it.getString(it.getColumnIndexOrThrow("userEmail")),
                    "featureName" to it.getString(it.getColumnIndexOrThrow("featureName")),
                    "why" to it.getString(it.getColumnIndexOrThrow("why")),
                    "updatedAtMillis" to it.getLong(it.getColumnIndexOrThrow("updatedAtMillis"))
                )
                val id = it.getString(it.getColumnIndexOrThrow("draftId"))
                val ok = runCatching {
                    draftsCol.document(id).set(payload, SetOptions.merge()).await()
                }.isSuccess
                if (ok) { markSynced(id); sent++ } else break
            }
        }
        sent
    }
}
