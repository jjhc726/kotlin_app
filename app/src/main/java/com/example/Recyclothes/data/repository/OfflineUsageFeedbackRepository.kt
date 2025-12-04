package com.example.Recyclothes.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.Recyclothes.data.local.db.UsageFeedbackDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class OfflineUsageFeedbackRepository(ctx: Context) {
    private val db = UsageFeedbackDbHelper(ctx).writableDatabase

    private data class Row(
        val requestId: String,
        val userEmail: String?,
        val featureName: String,
        val why: String
    )

    private fun Cursor.getStringOrNull(name: String): String? {
        val i = getColumnIndex(name)
        return if (i >= 0 && !isNull(i)) getString(i) else null
    }

    suspend fun queue(
        userEmail: String?,
        featureName: String,
        why: String
    ) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("requestId", UUID.randomUUID().toString())
            put("userEmail", userEmail)
            put("featureName", featureName)
            put("why", why)
            put("createdAtMillis", System.currentTimeMillis())
        }
        db.insert("offline_usage_feedback", null, cv)
    }

    private fun readBatch(limit: Int = 25): List<Row> {
        val rows = mutableListOf<Row>()
        val c = db.query(
            "offline_usage_feedback",
            arrayOf("requestId","userEmail","featureName","why"),
            null, null, null, null,
            "createdAtMillis ASC",
            limit.toString()
        )
        c.use {
            while (it.moveToNext()) {
                rows += Row(
                    requestId  = it.getStringOrNull("requestId")!!,
                    userEmail  = it.getStringOrNull("userEmail"),
                    featureName= it.getStringOrNull("featureName")!!,
                    why        = it.getStringOrNull("why")!!
                )
            }
        }
        return rows
    }

    private fun delete(requestId: String) {
        db.delete("offline_usage_feedback", "requestId=?", arrayOf(requestId))
    }

    suspend fun flushPending(onlineRepo: UsageFeedbackRepository): Int =
        withContext(Dispatchers.IO) {
            var sent = 0
            while (true) {
                val batch = readBatch()
                if (batch.isEmpty()) break
                var anyFailed = false
                for (r in batch) {
                    val ok = onlineRepo.submitOnline(r.userEmail, r.featureName, r.why)
                    if (ok) { delete(r.requestId); sent++ } else { anyFailed = true }
                }
                if (anyFailed) break
            }
            sent
        }
}
