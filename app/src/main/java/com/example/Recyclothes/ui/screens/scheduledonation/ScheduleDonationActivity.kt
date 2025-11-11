package com.example.Recyclothes.ui.screens.scheduledonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import androidx.compose.runtime.LaunchedEffect
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.utils.UsageTracker



class ScheduleDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecyclothesTheme {
                LaunchedEffect(Unit) {
                    UsageTracker.bump(FeatureId.SCHEDULE_DONATION_OPEN)
                }
                ScheduleDonationDesign()
            }
        }
    }
}
