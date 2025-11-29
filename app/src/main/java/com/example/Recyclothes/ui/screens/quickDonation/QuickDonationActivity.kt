package com.example.Recyclothes.ui.screens.quickDonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.viewmodel.DonationViewModel

class QuickDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val draftId = intent.getLongExtra("draft_id", -1)

        setContent {
            RecyclothesTheme {
                val donationViewModel: DonationViewModel = viewModel()

                LaunchedEffect(draftId) {
                    if (draftId != -1L) {
                        donationViewModel.loadDraft(draftId)
                    }
                }

                QuickDonationDesign(
                    viewModel = donationViewModel
                )
            }
        }
    }
}