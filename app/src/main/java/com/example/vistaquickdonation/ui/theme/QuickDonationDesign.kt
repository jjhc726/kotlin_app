package com.example.vistaquickdonation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickDonationDesign() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAFC7CA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Donate Your Clothing",
            fontSize = 24.sp,
            color = Color(0xFF003137)
        )
        Text(
            text = "Let’s find the perfect new home for your pre-loved items.",
            fontSize = 14.sp,
            color = Color(0xFF1B454B),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFBCEEF5)),
            contentAlignment = Alignment.Center
        ) {
            Text("Item Image (Required)", color = Color(0xFF003137))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Description (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Clothing Type (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Size (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Brand (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3E6F75),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Donation")
        }
    }
}
