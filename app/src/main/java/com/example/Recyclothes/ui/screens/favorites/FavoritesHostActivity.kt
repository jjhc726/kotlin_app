package com.example.Recyclothes.ui.screens.favorites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.viewmodel.CharityViewModel

class FavoritesHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                val charityVm: CharityViewModel = viewModel()
                FavoriteCharitiesScreen(
                    onOpenCharity = { id ->
                        val item = charityVm.charities.firstOrNull { it.id == id }
                        if (item != null) charityVm.selectCharity(item)
                    }
                )
            }
        }
    }
}
