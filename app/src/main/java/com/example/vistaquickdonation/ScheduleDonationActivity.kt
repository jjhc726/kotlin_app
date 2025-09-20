package com.example.vistaquickdonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class ScheduleDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                ScheduleDonationScreen()
            }
        }
    }
}

@Composable
fun ScheduleDonationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Aquí iría la vista de Schedule Donation", fontSize = 20.sp)
    }
}
