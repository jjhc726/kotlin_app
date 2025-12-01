package com.example.Recyclothes.ui.screens.favorites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.local.CharityEntity
import com.example.Recyclothes.data.local.toModel
import com.example.Recyclothes.ui.screens.charity.CharityProfileScreen
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class FavoritesHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                var selected by remember { mutableStateOf<CharityEntity?>(null) }

                if (selected == null) {
                    FavoritesScreen(
                        onOpenProfile = { selected = it }
                    )
                } else {
                    CharityProfileScreen(
                        charity = selected!!.toModel(),
                        onBackClick = { selected = null }
                    )
                }
            }
        }
    }
}
