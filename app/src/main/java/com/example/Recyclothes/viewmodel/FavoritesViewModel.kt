package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.repository.FavoriteCharitiesRepository
import com.example.Recyclothes.data.local.CharityEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FavoriteCharitiesRepository(app)

    private val _favorites = MutableStateFlow<List<CharityEntity>>(emptyList())
    val favorites: StateFlow<List<CharityEntity>> = _favorites

    private val _top3 = MutableStateFlow<List<Pair<CharityEntity, Long>>>(emptyList())

    init {
        viewModelScope.launch {
            repo.observeFavoriteIds().collectLatest { ids ->
                val list = repo.getFavoriteCharities(ids)
                _favorites.value = list
            }
        }
        viewModelScope.launch {
            val pairs = repo.top3()
            val entities = repo.getFavoriteCharities(pairs.map { it.first }.toSet())
            val map = entities.associateBy { it.id }
            _top3.value = pairs.mapNotNull { (id, count) ->
                map[id]?.let { it to count }
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }


}
