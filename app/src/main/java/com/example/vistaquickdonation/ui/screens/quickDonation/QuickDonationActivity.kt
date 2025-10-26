package com.example.vistaquickdonation.ui.screens.quickDonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import com.example.vistaquickdonation.viewmodel.DonationViewModel

class QuickDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                val donationViewModel: DonationViewModel = viewModel()

                QuickDonationDesign(
                    viewModel = donationViewModel
                )
            }
        }

    }
}