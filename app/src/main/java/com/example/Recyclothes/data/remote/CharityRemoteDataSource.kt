package com.example.Recyclothes.data.remote

import com.example.Recyclothes.data.model.Charity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CharityRemoteDataSource {
    private val col = Firebase.firestore.collection("Charities")

    private fun toIntSafe(any: Any?): Int? = when (any) {
        null -> null
        is Number -> any.toInt()
        is String -> any.filter(Char::isDigit).toIntOrNull()
        else -> null
    }

    suspend fun fetchAll(): List<Charity> = withContext(Dispatchers.IO) {
        val snap = runCatching { col.get().await() }.getOrNull() ?: return@withContext emptyList()

        snap.documents.mapIndexedNotNull { idx, doc ->
            val name = doc.getString("name")?.takeIf { it.isNotBlank() } ?: return@mapIndexedNotNull null

            val idFromField = toIntSafe(doc.get("id"))
            val idFromDoc   = doc.id.filter(Char::isDigit).toIntOrNull()
            val id = idFromField ?: idFromDoc ?: (1000 + idx)

            val cause    = doc.getString("cause") ?: "General"
            val schedule = doc.getString("schedule") ?: "Any time"

            Charity(
                id = id,
                name = name,
                description = "Cause: $cause • Schedule: $schedule",
                campaigns = listOf(cause, schedule)
            )
        }
    }
}
