package com.example.Recyclothes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.Recyclothes.utils.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    private val _justLostConnection = MutableStateFlow(false)
    val justLostConnection = _justLostConnection.asStateFlow()

    private val _justRegainedConnection = MutableStateFlow(false)
    val justRegainedConnection = _justRegainedConnection.asStateFlow()

    private val observer: NetworkObserver =
        NetworkObserver(application.applicationContext)

    private var currentState = false

    init {
        currentState = observer.isOnline()
        _isOnline.value = currentState

        observer.registerCallback(
            onAvailable = {
                if (!currentState) {
                    _justRegainedConnection.value = true
                }
                currentState = true
                _isOnline.value = true
            },
            onLost = {
                if (currentState) {
                    _justLostConnection.value = true
                }
                currentState = false
                _isOnline.value = false
            }
        )
    }
}
