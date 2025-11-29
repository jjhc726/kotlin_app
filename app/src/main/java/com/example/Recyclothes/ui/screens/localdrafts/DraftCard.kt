package com.example.Recyclothes.ui.screens.localdrafts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.Recyclothes.data.local.DraftDonationEntity
import com.example.Recyclothes.ui.theme.*

@Composable
fun DraftCard(
    draft: DraftDonationEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text("Description: ${draft.description}", color = DeepBlue)
                Text("Type: ${draft.clothingType}", color = DeepBlue)
                Text("Size: ${draft.size}", color = DeepBlue)
                Text("Brand: ${draft.brand}", color = DeepBlue)
                Text("Tags: ${draft.tags}", color = DeepBlue)
                Text("User: ${draft.userEmail}", color = DeepBlue)
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Edit draft",
                tint = DeepBlue,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
