package com.example.Recyclothes.ui.screens.scheduledonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class ScheduleDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecyclothesTheme {
                ScheduleDonationDesign()
            }
        }
    }
}
