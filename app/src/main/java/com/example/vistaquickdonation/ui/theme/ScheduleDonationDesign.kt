package com.example.vistaquickdonation.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.VistaQuickDonationTheme

class ScheduleDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VistaQuickDonationTheme {
                ScheduleDonationDesign()
            }
        }
    }
}

@Composable
fun ScheduleDonationDesign() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAFC7CA))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Schedule Your Donation",
            fontSize = 26.sp,
            color = Color(0xFF3E6F75)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Plan ahead when you want to deliver your items.",
            fontSize = 14.sp,
            color = Color(0xFF3E6F75)
        )

        // 👇 Aquí aumentamos el espacio antes del primer campo
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White, shape = RoundedCornerShape(6.dp))
                .padding(start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Donation Title (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White, shape = RoundedCornerShape(6.dp))
                .padding(start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Donation Date (Required)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White, shape = RoundedCornerShape(6.dp))
                .padding(start = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Donation Time (Optional)", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White, shape = RoundedCornerShape(6.dp))
                .padding(start = 8.dp, top = 8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text("Additional Notes", color = Color(0xFF6F9AA0), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF003137),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Confirm Schedule")
        }
    }
}
