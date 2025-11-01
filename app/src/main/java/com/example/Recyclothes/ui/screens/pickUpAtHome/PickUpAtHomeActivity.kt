package com.example.Recyclothes.ui.screens.pickUpAtHome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class PickUpAtHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecyclothesTheme {
                PickUpAtHomeScreen()
            }
        }
    }
}