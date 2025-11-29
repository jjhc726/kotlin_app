package com.example.Recyclothes.ui.screens.favorites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.ui.screens.charity.CharityProfileScreen
import com.example.Recyclothes.data.local.CharityEntity
import com.example.Recyclothes.data.local.toModel
import com.example.Recyclothes.viewmodel.CharityCatalogViewModel
import com.example.Recyclothes.viewmodel.FavoriteStatsViewModel

class FavoritesHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selected by remember { mutableStateOf<CharityEntity?>(null) }

            val statsVM: FavoriteStatsViewModel = viewModel()
            val top3 by statsVM.top3.collectAsState()

            val catalogVM: CharityCatalogViewModel = viewModel()
            val charities by catalogVM.charities.collectAsState()
            val nameOf: (Int) -> String = { id ->
                charities.firstOrNull { it.id == id }?.name ?: "Charity #$id"
            }

            if (selected == null) {
                FavoritesScreen(
                    onOpenProfile = { selected = it },
                    top = top3,
                    nameResolver = nameOf
                )
            } else {
                CharityProfileScreen(
                    charity = selected!!.toModel(),
                    onBackClick = { selected = null }
                )
            }


            /*RecyclothesTheme {
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
            }*/
        }
    }
}
