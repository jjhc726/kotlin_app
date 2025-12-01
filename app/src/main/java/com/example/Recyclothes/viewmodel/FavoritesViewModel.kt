package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.local.CharityEntity
import com.example.Recyclothes.data.remote.FavoriteStat
import com.example.Recyclothes.data.repository.FavoriteCharitiesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FavoriteCharitiesRepository(app)

    private val _favorites = MutableStateFlow<List<CharityEntity>>(emptyList())
    val favorites: StateFlow<List<CharityEntity>> = _favorites

    private val _top3 = MutableStateFlow<List<FavoriteStat>>(emptyList())
    val top3: StateFlow<List<FavoriteStat>> = _top3

    private val nameCache = mutableMapOf<Int, String>()

    init {
        viewModelScope.launch {
            repo.observeFavoriteIds().collectLatest { ids ->
                _favorites.value = repo.getFavoriteCharities(ids)
            }
        }
        viewModelScope.launch {
            _top3.value = repo.top3()
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }

    suspend fun resolveName(id: Int): String {
        nameCache[id]?.let { return it }
        val name = repo.getCharityName(id) ?: "Charity #$id"
        nameCache[id] = name
        return name
    }
}
