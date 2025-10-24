package com.example.vistaquickdonation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vistaquickdonation.ui.screens.QuickDonationDesign
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import com.google.firebase.FirebaseApp

class QuickDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        Log.d("FIREBASE_CHECK", "Firebase initialized: ${FirebaseApp.getApps(this).size} app(s)")

        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                val donationViewModel: DonationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                QuickDonationDesign(
                    viewModel = donationViewModel
                )
            }
        }

    }
}
