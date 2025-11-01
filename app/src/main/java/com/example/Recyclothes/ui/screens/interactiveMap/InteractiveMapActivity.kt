package com.example.Recyclothes.ui.screens.interactiveMap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class InteractiveMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecyclothesTheme {
                InteractiveMapScreen()
            }
        }
    }
}