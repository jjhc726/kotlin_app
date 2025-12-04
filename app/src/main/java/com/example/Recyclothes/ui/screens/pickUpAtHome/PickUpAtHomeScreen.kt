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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.Recyclothes.viewmodel.PickupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickUpAtHomeScreen(viewModel: PickupViewModel = viewModel()) {

    val context = LocalContext.current

    val address by viewModel.address.collectAsState()
    val date by viewModel.date.collectAsState()
    val hour by viewModel.hour.collectAsState()
    val donations by viewModel.donations.collectAsState()
    val networkAvailable by viewModel.networkStatus.collectAsState()
    val bannerDismissed = remember { mutableStateOf(false) }

    var selectedDonationId by remember { mutableStateOf<String?>(null) }
    var selectedDonation by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var showLoading by remember { mutableStateOf(false) }
    var showOfflineDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load donations on start
    LaunchedEffect(Unit) {
        viewModel.startNetworkObserver()
        viewModel.loadUserDonations()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.address.value = it },
                label = { Text("Enter your address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            DateField(date) { viewModel.date.value = it }

            Spacer(Modifier.height(14.dp))

            TimeField(hour) { viewModel.hour.value = it }

            Spacer(Modifier.height(20.dp))


            // --------------------------------------
            // DONATION DROPDOWN
            // --------------------------------------
            Text("Select one of your donations:", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(10.dp))

            Box {
                OutlinedTextField(
                    value = selectedDonation ?: "Choose donation",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    for (i in 0 until donations.size()) {
                        val donation = donations[i]
                        DropdownMenuItem(
                            text = { Text("${donation.clothingType} - ${donation.size}") },
                            onClick = {
                                selectedDonationId = i.toString()
                                selectedDonation = donation.clothingType + " - " + donation.size
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    val email = viewModel.getCurrentUserEmail()
                    viewModel.onPickupAtHomeSelected()

                    if (address.isBlank() || date.isBlank() || hour.isBlank()) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedDonationId == null) {
                        Toast.makeText(context, "Select a donation", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    showLoading = true

                    viewModel.submitPickup(
                        donationId = selectedDonationId!!,
                        userId = email,
                        onNoConnection = {
                            showLoading = false
                            viewModel.resetForm()
                            selectedDonationId = null
                            showOfflineDialog = true
                        },
                        onFinished = {
                            showLoading = false
                            viewModel.resetForm()
                            selectedDonationId = null
                            Toast.makeText(context, "Pickup created", Toast.LENGTH_LONG).show()
                        }
                    )

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = !showLoading
            ) {
                Text("Confirm Pickup")
            }

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(
                visible = !networkAvailable && !bannerDismissed.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier
                    .padding(bottom = 8.dp)
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
                            tint = Color(0xFF003366)
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