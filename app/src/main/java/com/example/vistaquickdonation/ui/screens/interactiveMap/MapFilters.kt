package com.example.vistaquickdonation.ui.screens.interactiveMap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.White
import com.example.vistaquickdonation.viewmodel.DonationMapViewModel

@Composable
fun MapFilters(viewModel: DonationMapViewModel) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(0.95f),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    label = "Cause",
                    value = viewModel.cause.value,
                    items = listOf("All", "Children", "Adults", "Emergency"),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.cause.value = it
                    viewModel.updateFilters()
                }
                FilterDropdown(
                    label = "Access",
                    value = viewModel.access.value,
                    items = listOf("All", "Accessible", "Not accessible"),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.access.value = it
                    viewModel.updateFilters()
                }
                FilterDropdown(
                    label = "Schedule",
                    value = viewModel.schedule.value,
                    items = listOf("All", "Morning", "Afternoon", "Night"),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.schedule.value = it
                    viewModel.updateFilters()
                }
            }
        }
    }
}

