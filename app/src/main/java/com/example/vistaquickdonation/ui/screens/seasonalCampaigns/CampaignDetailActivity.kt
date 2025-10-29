package com.example.vistaquickdonation.ui.screens.seasonalCampaigns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class CampaignDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val campaignId = intent.getIntExtra("campaignId", 0)

        setContent {
            VistaQuickDonationTheme {
                CampaignDetailScreen(campaignId = campaignId, onBack = { finish() })
            }
        }
    }
}