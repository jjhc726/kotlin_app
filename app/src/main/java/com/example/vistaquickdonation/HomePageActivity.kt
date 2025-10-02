package com.example.vistaquickdonation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.*

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VistaQuickDonationTheme {
                HomePageScreen()
            }
        }
    }
}

@Composable
fun HomePageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue) // 🎨 Fondo con color de la paleta
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a Quick Donation",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AquaLight),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Haz donaciones rápidas y sencillas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TealDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Aquí podrías redirigir a QuickDonationActivity */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MediumBlue)
                ) {
                    Text("Donar Ahora", color = White)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TealMedium),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Agenda tu próxima donación",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Aquí podrías redirigir a ScheduleDonationActivity */ },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
                ) {
                    Text("Agendar", color = White)
                }
            }
        }
    }
}
