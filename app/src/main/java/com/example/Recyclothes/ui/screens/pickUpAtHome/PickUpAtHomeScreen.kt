package com.example.Recyclothes.ui.screens.pickUpAtHome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PickUpAtHomeScreen() {
    val bgColor = Color(0xFFE6F0F2)
    val primaryColor = Color(0xFF003137)
    val accentColor = Color(0xFF0FA3B1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Inputs
        @Composable
        fun InputCard(label: String) {
            Text(label, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = primaryColor)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp))
                    .padding(start = 16.dp),
            )
            Spacer(Modifier.height(24.dp))
        }

        InputCard("Choose your location")
        InputCard("Select a Date")
        InputCard("Select a Time")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* acción */ },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Confirm", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }
}
