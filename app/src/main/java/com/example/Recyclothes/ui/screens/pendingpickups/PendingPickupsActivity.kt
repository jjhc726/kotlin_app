package com.example.Recyclothes.ui.screens.pendingpickups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class PendingPickupsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PendingPickupsScreen()
        }
    }
}
