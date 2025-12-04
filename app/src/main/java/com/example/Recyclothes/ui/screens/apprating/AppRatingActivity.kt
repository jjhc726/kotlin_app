package com.example.Recyclothes.ui.screens.apprating

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class AppRatingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme { AppRatingScreen() }
        }
    }
}
