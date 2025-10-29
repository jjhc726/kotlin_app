package com.example.vistaquickdonation.ui.screens.seasonalCampaigns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class SeasonalCampaignsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VistaQuickDonationTheme {
                SeasonalCampaignsScreen()
            }
        }
    }
}
