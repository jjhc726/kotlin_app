package com.example.Recyclothes.data.remote

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class FavoriteStat(val charityId: Int, val count: Long)

class FavoriteRemoteDataSource {

    suspend fun top3(): List<FavoriteStat> = withContext(Dispatchers.IO) {
        val cg = Firebase.firestore.collectionGroup("ids")
        val snap = runCatching { cg.whereEqualTo("fav", true).get().await() }.getOrNull()
            ?: return@withContext emptyList()

        val counts = mutableMapOf<Int, Long>()
        for (doc in snap.documents) {
            val id = doc.id.toIntOrNull() ?: continue
            counts[id] = (counts[id] ?: 0L) + 1L
        }

        counts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { FavoriteStat(charityId = it.key, count = it.value) }
    }
}
