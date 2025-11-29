package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FavoriteStat(val charityId: Int, val count: Int)

class FavoriteStatsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _top3 = MutableStateFlow<List<FavoriteStat>>(emptyList())
    val top3: StateFlow<List<FavoriteStat>> = _top3

    init {
        db.collection("favorite_counts")
            .orderBy("count", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { d ->
                    val id = d.getLong("charityId")?.toInt() ?: return@mapNotNull null
                    val c  = d.getLong("count")?.toInt() ?: 0
                    FavoriteStat(id, c)
                } ?: emptyList()
                _top3.value = list
            }
    }
}
