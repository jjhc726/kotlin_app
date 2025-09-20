package com.example.vistaquickdonation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharityProfileDesign() {
    val bgColor = Color(0xFFAFC7CA)
    val secondColor = Color(0xFF003137)

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Home", color = Color.Black)
                Text("Campaigns", color = Color.Black)
                Text("Profile", color = Color.Black)
                Text("Settings", color = Color.Black)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues)
        ) {
            /* Barra superior */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(secondColor)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Charity Profile",
                    fontSize =25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            /* Contenido scrolleable */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Imagen charity (placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Charity Name",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "This is a brief description about the charity organization, highlighting its mission and goals.",
                    fontSize = 16.sp,
                    color = Color(0xFF444444)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Current campaigns",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "• Campaign 1: Collection of used clothes",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    "• Campaign 2: Donations for families in need",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    "• Campaign 3: Textile recycling and reuse",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = { /* sin acción */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003137))
                ) {
                    Text("Donate", color = Color.White)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { /* sin acción */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003137))
                ) {
                    Text("Add to Favorite", color = Color.White)
                }

            }
        }
    }
}