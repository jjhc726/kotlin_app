package com.example.vistaquickdonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vistaquickdonation.ui.QuickDonationDesign
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class QuickDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                QuickDonationDesign()
            }
        }
    }
}
