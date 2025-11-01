package com.example.Recyclothes.ui.screens.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue

@Composable
fun HomeHeader(
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onMenuClick) {
            Text("≡", color = DeepBlue, fontSize = 22.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Recyclothes",
            color = DeepBlue,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onNotificationsClick) {
            Icon(Icons.Filled.Notifications, "Notifications", tint = DeepBlue)
        }
    }

    Text(
        "Donate easily, make a real impact.",
        style = MaterialTheme.typography.bodyMedium,
        color = MediumBlue,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
