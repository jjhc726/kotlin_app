package com.example.Recyclothes.ui.screens.pendingpickups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.local.PickupRequestEntity
import com.example.Recyclothes.viewmodel.PendingPickupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingPickupsScreen(viewModel: PendingPickupsViewModel = viewModel()) {

    val pendingList by viewModel.pendingPickups.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadPendingPickups()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Pickups") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(pendingList) { item ->
                PendingPickupCard(item)
            }
        }
    }
}

@Composable
fun PendingPickupCard(item: PickupRequestEntity) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Name: ${item.donationId}")
            Text("Address: ${item.address}")
            Text("Date: ${item.date}")
            Text("Hour: ${item.hour}")
        }
    }
}
