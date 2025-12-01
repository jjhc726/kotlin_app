package com.example.Recyclothes.ui.screens.pickUpAtHome

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Calendar
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.data.model.DonationPoint
import com.example.Recyclothes.ui.screens.main.MainNavigationActivity
import com.example.Recyclothes.viewmodel.PickupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUpAtHomeScreen(viewModel: PickupViewModel = viewModel()) {
    val context = LocalContext.current

    // ViewModel state
    val address by viewModel.address.collectAsState()
    val date by viewModel.date.collectAsState()
    val hour by viewModel.hour.collectAsState()
    val cause by viewModel.cause.collectAsState()
    val charities by viewModel.filteredCharities().collectAsState(initial = emptyList())
    val networkAvailable by viewModel.networkStatus.collectAsState()

    // UI state
    var selectedFoundation by remember { mutableStateOf<String?>(null) }
    var showLoading by remember { mutableStateOf(false) }
    var showOfflineDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val bannerDismissed = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startNetworkObserver()
    }
    // load charities once
    LaunchedEffect(Unit) {
        viewModel.loadCharities()
    }


    LaunchedEffect(networkAvailable) {
        if (networkAvailable) {
            bannerDismissed.value = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // ADDRESS
                OutlinedTextField(
                    value = address,
                    onValueChange = { viewModel.address.value = it },
                    label = { Text("Enter your address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                DateField(date = date, onDateSelected = { viewModel.date.value = it })

                Spacer(Modifier.height(12.dp))

                TimeField(time = hour, onTimeSelected = { viewModel.hour.value = it })

                Spacer(Modifier.height(12.dp))

                CauseSelector(selected = cause, onSelected = { viewModel.cause.value = it })

                Spacer(Modifier.height(20.dp))

                Text("Available foundations:", style = MaterialTheme.typography.titleMedium)

                CharitySelector(
                    charities = charities,
                    selectedId = selectedFoundation,
                    onSelected = { selectedFoundation = it }
                )

                Spacer(Modifier.height(60.dp))

                Button(
                    onClick = {
                        val userEmail = viewModel.getCurrentUserEmail()

                        if (address.isBlank()) {
                            Toast.makeText(context, "Please enter your address", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (date.isBlank()) {
                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (hour.isBlank()) {
                            Toast.makeText(context, "Please select a time", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (cause.isBlank()) {
                            Toast.makeText(context, "Please select a cause", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (selectedFoundation.isNullOrBlank()) {
                            Toast.makeText(
                                context,
                                "Please select a foundation",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        showLoading = true

                        viewModel.submitPickup(
                            foundationId = selectedFoundation!!,
                            userId = userEmail,
                            onNoConnection = {
                                showLoading = false
                                viewModel.resetForm()
                                selectedFoundation = null
                                showOfflineDialog = true
                            },
                            onFinished = {
                                showLoading = false
                                viewModel.resetForm()
                                selectedFoundation = null
                                Toast.makeText(
                                    context,
                                    "Pickup created successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                        viewModel.onPickupAtHomeSelected()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !showLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B454B),
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirm Pickup")
                }
            }
            AnimatedVisibility(
                visible = !networkAvailable && !bannerDismissed.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .padding(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(4.dp)
                        .clickable { bannerDismissed.value = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF003366) // DeepBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You are offline. Showing local charities.",
                            color = Color(0xFF003366),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            }

            if (showLoading) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Processing") },
                    text = { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) },
                    confirmButton = {}
                )
            }

            if (showOfflineDialog) {
                AlertDialog(
                    onDismissRequest = { showOfflineDialog = false },
                    title = { Text("Offline") },
                    text = { Text("You are currently offline. Your pickup will be sent once connection is restored.") },
                    confirmButton = {
                        TextButton(onClick = { showOfflineDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
    }
}


@Composable
private fun DateField(date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text("Select a date") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(context, { _, y, m, d ->
                    val cal = Calendar.getInstance().apply {
                        set(y, m, d)
                    }
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    onDateSelected(format.format(cal.time))
                }, year, month, day).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "date")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TimeField(time: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    OutlinedTextField(
        value = time,
        onValueChange = {},
        label = { Text("Select a time") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                android.app.TimePickerDialog(context, { _, h, m ->
                    val formatted = String.format("%02d:%02d", h, m)
                    onTimeSelected(formatted)
                }, hour, minute, true).show()
            }) {
                Icon(Icons.Default.AccessTime, contentDescription = "time")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CauseSelector(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("children", "adults", "emergency")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(option.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
fun CharitySelector(charities: List<DonationPoint>, selectedId: String?, onSelected: (String) -> Unit) {
    Column {
        if (charities.isEmpty()) {
            Text("No foundations for this cause.")
        } else {
            charities.forEach { f ->
                FilterChip(
                    selected = selectedId == f.id,
                    onClick = { onSelected(f.id) },
                    label = { Text(f.name) }
                )
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}
