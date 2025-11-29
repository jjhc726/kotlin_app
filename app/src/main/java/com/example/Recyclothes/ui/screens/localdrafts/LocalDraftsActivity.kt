package com.example.Recyclothes.ui.screens.localdrafts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class LocalDraftsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecyclothesTheme {
                LocalDraftsScreen(
                    repo = DonationRepository(this),
                    onBack = { finish() }
                )
            }
        }
    }
}
