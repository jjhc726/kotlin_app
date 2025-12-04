package com.example.Recyclothes.ui.screens.seasonalCampaigns.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OfflineBanner(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onDismiss() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Offline",
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF003366)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "You are offline. Showing cached campaigns.",
                color = Color(0xFF003366),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
