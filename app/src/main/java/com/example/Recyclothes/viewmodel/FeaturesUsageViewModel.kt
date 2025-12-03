package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.model.UsageFeedback
import com.example.Recyclothes.data.repository.FeaturesUsageRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FeaturesUsageViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FeaturesUsageRepository(app)
    private val net  = ConnectivityObserver(app)

    init {
        net.onlineFlow().onEach { isOnline ->
            if (isOnline) viewModelScope.launch { repo.flushPending() }
        }.launchIn(viewModelScope)
    }

    fun submitFeedback(
        userEmail: String,
        featureName: String,
        why: String,
        onOnlineOk: () -> Unit,
        onQueuedOffline: () -> Unit
    ) {
        viewModelScope.launch {
            val ok = repo.submitFeedback(
                UsageFeedback(
                    userEmail  = userEmail,
                    featureName = featureName,
                    why        = why,
                    createdAt  = System.currentTimeMillis()
                )
            )
            if (ok) onOnlineOk() else onQueuedOffline()
        }
    }
}
