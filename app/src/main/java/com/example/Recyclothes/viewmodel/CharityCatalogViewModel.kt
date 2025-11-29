package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.Charity
import com.example.Recyclothes.data.repository.CharityCacheRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CharityCatalogViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = CharityCacheRepository(app)

    val charities: StateFlow<List<Charity>> =
        repo.observe().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repo.ensureFresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repo.refreshNow()
        }
    }

}
