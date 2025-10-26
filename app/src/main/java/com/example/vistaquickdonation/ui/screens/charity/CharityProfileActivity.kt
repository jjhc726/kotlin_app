package com.example.vistaquickdonation.ui.screens.charity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class CharityProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                CharityProfileScreen()
            }
        }
    }
}