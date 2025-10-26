package com.example.vistaquickdonation.ui.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.Secondary
import com.example.vistaquickdonation.ui.screens.charity.CharityProfileActivity
import com.example.vistaquickdonation.ui.screens.interactiveMap.InteractiveMapActivity
import com.example.vistaquickdonation.ui.screens.pickUpAtHome.PickUpAtHomeActivity
import com.example.vistaquickdonation.ui.screens.quickDonation.QuickDonationActivity
import com.example.vistaquickdonation.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.vistaquickdonation.ui.theme.*
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

    val sessionEmail = remember {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("email", null)
    }

    val donationVM = viewModel<DonationViewModel>()
    val monthlyDonations by donationVM.monthlyDonations.collectAsState()

    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val lastDonationText by notificationsVM.lastDonationText.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }
    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(sessionEmail) {
        donationVM.loadThisMonthDonations()
        sessionEmail?.let { notificationsVM.start(it) }
    }

    fun go(target: Class<*>) = context.startActivity(Intent(context, target))

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = White, drawerTonalElevation = 4.dp) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Menu",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = DeepBlue
                )
                NavigationDrawerItem(
                    label = { Text("Open Interactive Map") },
                    selected = false,
                    onClick = {
                        logDonationPreference("interactiveMap")
                        scope.launch { drawerState.close() }
                        go(InteractiveMapActivity::class.java)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Go to Charity Profile") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        go(CharityProfileActivity::class.java)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Go to Pick Up At Home") },
                    selected = false,
                    onClick = {
                        logDonationPreference("pickupAtHome")
                        scope.launch { drawerState.close() }
                        go(PickUpAtHomeActivity::class.java)
                    }
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
                    modifier = Modifier.fillMaxWidth().height(160.dp),
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
                Text("This Month's Donations", color = DeepBlue)
                if (monthlyDonations.isEmpty()) {
                    Text("No donations yet this month", color = Secondary)
                } else {
                    monthlyDonations.forEach {
                        Text("- ${it.description} (${it.clothingType})", color = DeepBlue)
                    }
                }

                Spacer(Modifier.height(8.dp))
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
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
