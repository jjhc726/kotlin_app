package com.example.Recyclothes.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.Recyclothes.Secondary
import com.example.Recyclothes.data.model.AppNotification
import com.example.Recyclothes.ui.theme.DeepBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    notifications: List<AppNotification>
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Notifications",
                color = DeepBlue,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Text(
                    text = "You'll see your latest alerts here.",
                    color = Secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                notifications.forEach { notification ->
                    Text(
                        text = "• ${notification.title} — ${notification.body}",
                        color = DeepBlue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}
