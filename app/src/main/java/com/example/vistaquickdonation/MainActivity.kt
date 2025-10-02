package com.example.vistaquickdonation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
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
                    onHomePageClick = { // ✅ nuevo botón
                        startActivity(Intent(this, HomePageActivity::class.java))
                    }
                )
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
    onHomePageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onQuickDonationClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Quick Donation")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onScheduleDonationClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Schedule Donation")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Register")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onCharityProfileClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Charity Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPickUpAtHomeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a PickUpAtHome")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Nuevo botón para ir a la página principal
        Button(onClick = onHomePageClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Página Principal")
        }
    }
}
