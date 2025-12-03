package com.example.Recyclothes.connectivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectivityBanner(online: Boolean) {
    val bg = if (online) Color(0xFF2ECC71) else Color(0xFFE74C3C)
    val msg = if (online) "Online" else "No internet connection"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, color = Color.White, fontSize = 13.sp)
    }
}
