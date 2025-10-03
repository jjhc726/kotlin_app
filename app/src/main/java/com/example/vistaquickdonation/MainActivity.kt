package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

private val Bg = Color(0xFFAFC7CA)      // fondo
private val Primary = Color(0xFF003137) // primario (botones / títulos)
private val Secondary = Color(0xFF6F9AA0) // secundario (subtítulos)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    MainScreen(
                        onQuickDonationClick = {
                            startActivity(Intent(this, QuickDonationActivity::class.java))
                        },
                        onScheduleDonationClick = {
                            startActivity(Intent(this, ScheduleDonationActivity::class.java))
                        },
                        onLoginClick = {
                            startActivity(Intent(this, LoginActivity::class.java))
                        },
                        onRegisterClick = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        },
                        onCharityProfileClick = {
                            startActivity(Intent(this, CharityProfileActivity::class.java))
                        },
                        onPickUpAtHomeClick = {
                            startActivity(Intent(this, PickUpAtHomeActivity::class.java))
                        },
                        onHomePageClick = {
                            startActivity(Intent(this, HomePageActivity::class.java))
                        },
                        onDonationMapClick = {
                            startActivity(Intent(this, InteractiveMapActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onQuickDonationClick: () -> Unit,
    onScheduleDonationClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onCharityProfileClick: () -> Unit,
    onPickUpAtHomeClick: () -> Unit,
    onHomePageClick: () -> Unit,
    onDonationMapClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Recyclothes",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Donate easily, make a real impact.",
            style = MaterialTheme.typography.bodyMedium,
            color = Secondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeButton(text = "Go to Quick Donation", onClick = onQuickDonationClick)
                HomeButton(text = "Go to Schedule Donation", onClick = onScheduleDonationClick)
                DividerSpacer()

                HomeButton(text = "Go to Login", onClick = onLoginClick, tonal = true)
                HomeButton(text = "Go to Register", onClick = onRegisterClick, tonal = true)
                DividerSpacer()

                HomeButton(text = "Go to Charity Profile", onClick = onCharityProfileClick)
                HomeButton(text = "Go to Donation Map", onClick = onDonationMapClick)
                HomeButton(text = "Go to PickUp At Home", onClick = onPickUpAtHomeClick)
                HomeButton(text = "Go to Home Page", onClick = onHomePageClick)
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Made to reduce textile waste",
            style = MaterialTheme.typography.bodyMedium,
            color = Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun HomeButton(
    text: String,
    onClick: () -> Unit,
    tonal: Boolean = false
) {
    val colors = if (tonal)
        ButtonDefaults.filledTonalButtonColors(
            containerColor = Secondary,
            contentColor = Color.White
        )
    else
        ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.White
        )

    Button(
        onClick = onClick,
        colors = colors,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun DividerSpacer() {
    Spacer(Modifier.height(8.dp))
}