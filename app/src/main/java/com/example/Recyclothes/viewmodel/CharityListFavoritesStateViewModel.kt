package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.repository.FavoriteCharitiesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharityListFavoritesStateViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FavoriteCharitiesRepository(app)

    val favoriteIds: StateFlow<Set<Int>> =
        repo.observeFavoriteIds().stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    fun toggle(id: Int) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }
}
