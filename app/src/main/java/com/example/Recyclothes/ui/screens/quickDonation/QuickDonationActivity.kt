package com.example.Recyclothes.ui.screens.quickDonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.viewmodel.DonationViewModel

class QuickDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            RecyclothesTheme{
                val donationViewModel: DonationViewModel = viewModel()

                QuickDonationDesign(
                    viewModel = donationViewModel
                )
            }
        }

    }
}