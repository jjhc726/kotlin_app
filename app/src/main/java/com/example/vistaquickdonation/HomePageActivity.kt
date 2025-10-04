package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme
import kotlinx.coroutines.launch
import com.example.vistaquickdonation.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore


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

@Composable
fun HomePageScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun go(clz: Class<*>) {
        context.startActivity(Intent(context, clz))
    }

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top bar with menu button
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
                        style = MaterialTheme.typography.headlineMedium
                    )
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
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    // Replace R.drawable.home_banner with your actual asset name
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Home banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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

                Spacer(Modifier.weight(1f))

                Text(
                    text = "Made to reduce textile waste",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumBlue,
                    modifier = Modifier.padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
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
