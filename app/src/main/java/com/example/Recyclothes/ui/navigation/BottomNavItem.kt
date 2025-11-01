package com.example.Recyclothes.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    object Map : BottomNavItem("Map", Icons.Default.Map)
    object Charities : BottomNavItem("Charities", Icons.Default.Favorite)
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object PickUp : BottomNavItem("PickUp", Icons.Default.LocalShipping)
    object SeasonalCampaigns : BottomNavItem("Campaigns", Icons.Default.Star)

    companion object {
        fun allItems(): List<BottomNavItem> =
            listOf(Map, Charities, Home, PickUp, SeasonalCampaigns)
    }
}
