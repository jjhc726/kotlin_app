package com.example.vistaquickdonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vistaquickdonation.ui.screens.DonationMapScreen
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class InteractiveMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                DonationMapScreen()
            }
        }
    }
}