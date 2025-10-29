package com.example.vistaquickdonation.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.Secondary
import com.example.vistaquickdonation.ui.theme.AquaLight
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.MediumBlue
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.ui.theme.TealMedium
import com.example.vistaquickdonation.ui.theme.White

@Composable
fun HomeContent(
    topDonors: List<com.example.vistaquickdonation.viewmodel.TopDonor>,
    lastDonationText: String?,
    onQuickDonate: () -> Unit,
    onSchedule: () -> Unit
) {
    Spacer(Modifier.height(16.dp))
    FeatureCard(
        container = AquaLight,
        titleColor = TealDark,
        title = "Make quick and simple donations",
        buttonText = "Donate Now",
        buttonColor = MediumBlue,
        onClick = onQuickDonate
    )
    Spacer(Modifier.height(16.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AquaLight)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                Icons.Rounded.Checkroom,
                contentDescription = "Clothes banner",
                tint = TealDark,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }

    Spacer(Modifier.height(16.dp))
    FeatureCard(
        container = TealMedium,
        titleColor = White,
        title = "Schedule your next donation",
        buttonText = "Schedule",
        buttonColor = DeepBlue,
        onClick = onSchedule
    )

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Top 5 Donors",
        style = MaterialTheme.typography.titleMedium,
        color = DeepBlue,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    if (topDonors.isEmpty()) {
        Text("No donations recorded yet", color = Secondary)
    } else {
        topDonors.forEachIndexed { index, donor ->
            Text(
                text = "${index + 1}. ${donor.name} (${donor.totalDonations})",
                color = DeepBlue,
                fontSize = 15.sp
            )
        }
    }

    Spacer(Modifier.height(12.dp))
    Text("Last donation: ${lastDonationText ?: "—"}", color = DeepBlue)
    Spacer(Modifier.height(12.dp))
    Text(
        "Made to reduce textile waste",
        color = MediumBlue,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}
