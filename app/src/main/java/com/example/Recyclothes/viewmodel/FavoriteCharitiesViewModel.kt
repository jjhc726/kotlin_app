package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.data.repository.CharityCacheRepository
import com.example.Recyclothes.data.repository.FavoriteCharitiesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoriteCharitiesViewModel(app: Application) : AndroidViewModel(app) {

    private val favRepo = FavoriteCharitiesRepository(app)
    private val charityRepo = CharityCacheRepository(app)

    private val _ui = MutableStateFlow<List<Charity>>(emptyList())
    val ui: StateFlow<List<Charity>> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            favRepo.observeFavoriteIds().collect { ids ->
                val list = charityRepo.observe()
                    .first()
                    .filter { it.id in ids }
                    .sortedBy { it.name }
                _ui.value = list
            }
        }
        viewModelScope.launch { charityRepo.ensureFresh() }
    }
}
