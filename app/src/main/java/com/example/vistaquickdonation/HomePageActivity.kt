package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.*
import com.example.vistaquickdonation.viewmodel.DonationViewModel
import com.example.vistaquickdonation.viewmodel.NotificationsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.rounded.Checkroom



class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VistaQuickDonationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = SoftBlue) {
                    HomePageScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    val sessionEmail = remember {
        context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE)
            .getString("email", null)
    }


    val donationViewModel = viewModel<DonationViewModel>()
    val monthlyDonations by donationViewModel.monthlyDonations.collectAsState()


    val notificationsVM = viewModel<NotificationsViewModel>()
    val notifications by notificationsVM.notifications.collectAsState()
    val lastDonationText by notificationsVM.lastDonationText.collectAsState()
    val scrollState = rememberScrollState()


    LaunchedEffect(sessionEmail) {
        donationViewModel.loadThisMonthDonations()
        sessionEmail?.let { notificationsVM.start(it) }
    }

    fun go(clz: Class<*>) = context.startActivity(Intent(context, clz))


    var showNotifications by remember { mutableStateOf(false) }
    val notifSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = White,
                drawerTonalElevation = 4.dp
            ) {
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
                Spacer(Modifier.height(12.dp))
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


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
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
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = DeepBlue
                        )
                    }
                }

                Text(
                    text = "Donate easily, make a real impact.",
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
                    colors = CardDefaults.cardColors(containerColor = AquaLight),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Checkroom,
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
                    text = "This Month's Donations",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepBlue,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (monthlyDonations.isEmpty()) {
                    Text("No donations yet this month", color = Secondary)
                } else {
                    monthlyDonations.forEach { donation ->
                        Text(
                            "- ${donation.description} (${donation.clothingType})",
                            color = DeepBlue,
                            fontSize = 14.sp
                        )
                    }
                }


                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Last donation: ${lastDonationText ?: "—"}",
                    color = DeepBlue,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))



                Text(
                    text = "Made to reduce textile waste",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumBlue,
                    modifier = Modifier.padding(bottom = 12.dp),
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
                        Text(
                            "Notifications",
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepBlue
                        )
                        Spacer(Modifier.height(8.dp))

                        if (notifications.isEmpty()) {
                            Text("You'll see your latest alerts here.", color = Secondary)
                        } else {
                            notifications.forEach { n ->
                                Text("• ${n.title} — ${n.body}", color = DeepBlue)
                                Spacer(Modifier.height(6.dp))
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    container: Color,
    titleColor: Color,
    title: String,
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth(0.6f)
            ) {
                Text(buttonText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

fun logDonationPreference(type: String) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("Engagement").document("donationPreferences")

    db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)
        val current = snapshot.getLong(type) ?: 0
        transaction.update(docRef, type, current + 1)
    }.addOnFailureListener {
        val initialData = hashMapOf(
            "pickupAtHome" to 0,
            "interactiveMap" to 0
        )
        docRef.set(initialData)
    }
}
