package com.example.Recyclothes.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.repository.UserRepository
import com.example.Recyclothes.ui.components.BottomNavigationBar
import com.example.Recyclothes.ui.navigation.BottomNavItem
import com.example.Recyclothes.ui.screens.charity.CharityListScreen
import com.example.Recyclothes.ui.screens.charity.CharityProfileScreen
import com.example.Recyclothes.ui.screens.home.HomePageScreen
import com.example.Recyclothes.ui.screens.interactiveMap.InteractiveMapScreen
import com.example.Recyclothes.ui.screens.login.LoginActivity
import com.example.Recyclothes.ui.screens.pickUpAtHome.PickUpAtHomeScreen
import com.example.Recyclothes.ui.screens.seasonalCampaigns.SeasonalCampaignsScreen
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.ui.theme.RecyclothesTheme
import com.example.Recyclothes.viewmodel.CharityViewModel
import com.example.Recyclothes.viewmodel.NotificationsViewModel
import com.example.Recyclothes.viewmodel.SeasonalCampaignsViewModel

class MainNavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                MainNavigationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen() {
    val context = LocalContext.current
    val userRepo = remember { UserRepository() }
    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val charityVM = viewModel<CharityViewModel>()
    val seasonalVM = viewModel<SeasonalCampaignsViewModel>()

    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    var expanded by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealDark,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    if (selectedItem == BottomNavItem.Home) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Account",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Log out") },
                                    onClick = {
                                        expanded = false
                                        userRepo.signOut()
                                        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                                        prefs.edit { clear() }
                                        val intent = Intent(context, LoginActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (selectedItem) {
                                BottomNavItem.Home -> "Recyclothes"
                                BottomNavItem.Map -> "Donation Map"
                                BottomNavItem.Charities -> "Charities"
                                BottomNavItem.SeasonalCampaigns -> "Seasonal Campaigns"
                                BottomNavItem.PickUp -> "Pick Up at Home"
                            },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (selectedItem == BottomNavItem.Home) {
                        IconButton(onClick = { showNotifications = true }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = BottomNavItem.allItems(),
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                BottomNavItem.Home -> HomePageScreen()
                BottomNavItem.Map -> InteractiveMapScreen()
                BottomNavItem.Charities -> {
                    val selectedCharity = charityVM.selectedCharity
                    if (selectedCharity == null) {
                        CharityListScreen(
                            charities = charityVM.charities,
                            onCharityClick = { charityVM.selectCharity(it) }
                        )
                    } else {
                        CharityProfileScreen(
                            charity = selectedCharity,
                            onBackClick = { charityVM.goBack() }
                        )
                    }
                }
                BottomNavItem.SeasonalCampaigns -> SeasonalCampaignsScreen(viewModel = seasonalVM)
                BottomNavItem.PickUp -> PickUpAtHomeScreen()
            }

            if (showNotifications) {
                ModalBottomSheet(
                    onDismissRequest = { showNotifications = false },
                    sheetState = notifSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Notifications", color = DeepBlue)
                        if (notifications.isEmpty()) {
                            Text("You'll see your latest alerts here.", color = MediumBlue)
                        } else {
                            notifications.forEach {
                                Text("• ${it.title} — ${it.body}", color = DeepBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}
