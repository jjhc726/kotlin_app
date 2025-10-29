package com.example.vistaquickdonation.ui.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.vistaquickdonation.data.repository.UserRepository
import com.example.vistaquickdonation.ui.screens.charity.CharityProfileActivity
import com.example.vistaquickdonation.ui.screens.interactiveMap.InteractiveMapActivity
import com.example.vistaquickdonation.ui.screens.login.LoginActivity
import com.example.vistaquickdonation.ui.screens.pickUpAtHome.PickUpAtHomeActivity
import com.example.vistaquickdonation.utils.logDonationPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeDrawer(
    drawerTextColor: Color,
    drawerBgColor: Color,
    context: Context,
    scope: CoroutineScope,
    drawerState: DrawerState,
    userRepo: UserRepository
) {
    ModalDrawerSheet(
        drawerContainerColor = drawerBgColor,
        drawerTonalElevation = 4.dp
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            "Menu",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = drawerTextColor
        )

        NavigationDrawerItem(
            label = { Text("Open Interactive Map", color = drawerTextColor) },
            selected = false,
            onClick = {
                logDonationPreference("interactiveMap")
                scope.launch {
                    drawerState.close()
                    context.startActivity(Intent(context, InteractiveMapActivity::class.java))
                }
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = drawerBgColor,
                selectedContainerColor = drawerBgColor
            )
        )

        NavigationDrawerItem(
            label = { Text("Go to Charity Profile", color = drawerTextColor) },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    context.startActivity(Intent(context, CharityProfileActivity::class.java))
                }
            }
        )

        NavigationDrawerItem(
            label = { Text("Go to Pick Up At Home", color = drawerTextColor) },
            selected = false,
            onClick = {
                logDonationPreference("pickupAtHome")
                scope.launch {
                    drawerState.close()
                    context.startActivity(Intent(context, PickUpAtHomeActivity::class.java))
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            label = { Text("Sign Out", color = drawerTextColor) },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    userRepo.signOut()
                    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    prefs.edit { clear() }
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }
            }
        )
    }
}
