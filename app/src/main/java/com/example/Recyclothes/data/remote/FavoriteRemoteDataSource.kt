package com.example.Recyclothes.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FavoriteRemoteDataSource {
    private val col = Firebase.firestore.collection("favorite_charities")


    suspend fun top3(): List<Pair<Int, Long>> {
        val snap = col.orderBy("count", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .await()
        return snap.documents.mapNotNull { d ->
            val id = d.id.toIntOrNull() ?: return@mapNotNull null
            val count = (d.getLong("count") ?: 0L).coerceAtLeast(0L)
            id to count
        }
    }
}
