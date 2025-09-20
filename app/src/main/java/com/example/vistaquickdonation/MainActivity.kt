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
                        startActivity(
                            Intent(this, QuickDonationActivity::class.java)
                        )
                    },
                    onScheduleDonationClick = {
                        startActivity(
                            Intent(this, ScheduleDonationActivity::class.java)
                        )
                    },
                    onLoginClick = {
                        startActivity(
                            Intent(this, LoginActivity::class.java) // ✅ Nuevo botón
                        )
                    },
                    onRegisterClick = {
                        startActivity(
                            Intent(this, RegisterActivity::class.java)
                        )
                    },
                    onCharityProfileClick = {
                        startActivity(
                            Intent(this, CharityProfileActivity::class.java)
                        )
                    } ,
                    onPickUpAtHomeClick = {
                        startActivity(
                            Intent(this, PickUpAtHomeActivity::class.java)
                        )
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
    onPickUpAtHomeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onQuickDonationClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Quick Donation")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onScheduleDonationClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Schedule Donation")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Nuevo botón para Login
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Login")
        }
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Register")
        }

        Button(
            onClick = onCharityProfileClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Charity Profile")
        }
        Button(
            onClick = onPickUpAtHomeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a PickUpAtHome")
        }
    }
}
