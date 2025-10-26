package com.example.vistaquickdonation.ui.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.Secondary
import com.example.vistaquickdonation.data.repository.UserRepository
import com.example.vistaquickdonation.ui.screens.charity.CharityProfileActivity
import com.example.vistaquickdonation.ui.screens.interactiveMap.InteractiveMapActivity
import com.example.vistaquickdonation.ui.screens.login.LoginActivity
import com.example.vistaquickdonation.ui.screens.pickUpAtHome.PickUpAtHomeActivity
import com.example.vistaquickdonation.ui.screens.quickDonation.QuickDonationActivity
import com.example.vistaquickdonation.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.vistaquickdonation.ui.theme.AquaLight
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.MediumBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.ui.theme.TealMedium
import com.example.vistaquickdonation.ui.theme.White
import com.example.vistaquickdonation.utils.logDonationPreference
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import com.example.vistaquickdonation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val donationVM = viewModel<DonationViewModel>()
    val topDonors by donationVM.topDonors.collectAsState()

    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val lastDonationText by notificationsVM.lastDonationText.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }
    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val userRepo = remember { UserRepository() }
    val sessionEmail = remember { userRepo.currentEmail() }

    LaunchedEffect(Unit) {
        donationVM.loadTopDonors()
        sessionEmail?.let { notificationsVM.start(it) }
    }

    fun go(target: Class<*>) = context.startActivity(Intent(context, target))

    val drawerTextColor = Color(0xFF003137)
    val drawerBgColor = Color.White

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
                        scope.launch { drawerState.close() }
                        go(InteractiveMapActivity::class.java)
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = drawerBgColor,
                        selectedContainerColor = drawerBgColor,
                        selectedTextColor = drawerTextColor,
                        unselectedTextColor = drawerTextColor,
                        selectedIconColor = drawerTextColor,
                        unselectedIconColor = drawerTextColor
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Go to Charity Profile", color = drawerTextColor) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        go(CharityProfileActivity::class.java)
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = drawerTextColor,
                        unselectedTextColor = drawerTextColor
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Go to Pick Up At Home", color = drawerTextColor) },
                    selected = false,
                    onClick = {
                        logDonationPreference("pickupAtHome")
                        scope.launch { drawerState.close() }
                        go(PickUpAtHomeActivity::class.java)
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = drawerTextColor,
                        unselectedTextColor = drawerTextColor
                    )
                )

                Spacer(Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("Sign Out", color = drawerTextColor) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            userRepo.signOut()

                            val prefs =
                                context.getSharedPreferences("session", Context.MODE_PRIVATE)
                            prefs.edit { clear() }

                            val intent =
                                Intent(context, LoginActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            context.startActivity(intent)
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = drawerTextColor,
                        unselectedTextColor = drawerTextColor
                    )
                )
            }
        }
    ) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftBlue)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { scope.launch { drawerState.open() } }) {
                        Text("≡", color = DeepBlue, fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Recyclothes",
                        color = DeepBlue,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showNotifications = true }) {
                        Icon(Icons.Filled.Notifications, "Notifications", tint = DeepBlue)
                    }
                }

                Text(
                    "Donate easily, make a real impact.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))
                FeatureCard(
                    container = AquaLight,
                    titleColor = TealDark,
                    title = "Make quick and simple donations",
                    buttonText = "Donate Now",
                    buttonColor = MediumBlue,
                    onClick = { go(QuickDonationActivity::class.java) }
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AquaLight)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Rounded.Checkroom,
                            contentDescription = "Clothes banner",
                            tint = TealDark,
                            modifier = Modifier.fillMaxSize(0.5f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                FeatureCard(
                    container = TealMedium,
                    titleColor = White,
                    title = "Schedule your next donation",
                    buttonText = "Schedule",
                    buttonColor = DeepBlue,
                    onClick = { go(ScheduleDonationActivity::class.java) }
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Top 5 Donors",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepBlue,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (topDonors.isEmpty()) {
                    Text("No donations recorded yet", color = Secondary)
                } else {
                    topDonors.forEachIndexed { index, donor ->
                        Text(
                            text = "${index + 1}. ${donor.name} (${donor.totalDonations})",
                            color = DeepBlue,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Last donation: ${lastDonationText ?: "—"}", color = DeepBlue)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Made to reduce textile waste",
                    color = MediumBlue,
                    textAlign = TextAlign.Center
                )
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
                        Spacer(Modifier.height(8.dp))
                        if (notifications.isEmpty()) {
                            Text("You'll see your latest alerts here.", color = Secondary)
                        } else {
                            notifications.forEach {
                                Text("• ${it.title} — ${it.body}", color = DeepBlue)
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
