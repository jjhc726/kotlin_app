package com.example.vistaquickdonation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.MarkerState
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.ui.theme.White
import com.example.vistaquickdonation.viewmodel.DonationMapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationMapScreen(viewModel: DonationMapViewModel = viewModel()) {

    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.checkPermission()
            viewModel.requestLocation()
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.checkPermission()) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.requestLocation()
        }
    }

    val hasPermission by viewModel.hasLocationPermission
    val userLocation by viewModel.userLocation
    val visiblePoints by viewModel.visiblePoints
    val selectedMarkerState by viewModel.selectedMarkerState
    val currentnearestPoint by viewModel.currentnearestPoint

    val markerStates = remember { mutableMapOf<String, MarkerState>()}
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation, visiblePoints, isMapLoaded) {
            if (!isMapLoaded) return@LaunchedEffect
            delay(200)
            viewModel.findAndShowClosestPoint(markerStates)
    }

    LaunchedEffect(selectedMarkerState) {
        selectedMarkerState?.showInfoWindow()
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Donation Map") },
                modifier = Modifier.height(100.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealDark,
                    titleContentColor = White
                )
            )
        }
    ) { innerPadding ->
        Box(Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            if (hasPermission) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    onMapLoaded = { isMapLoaded = true },
                ) {
                    visiblePoints.forEach { p ->
                        val markerState = remember (p.id){ MarkerState(position = p.position) }
                        markerStates[p.id] = markerState
                        val isNearest = p == currentnearestPoint

                        MarkerInfoWindow(
                            state = markerState,
                            title = p.name,
                            snippet = "${p.cause} • ${p.schedule}${if (p.accessible) " • Accessible" else "• Not Accessible"}",
                            onClick = {
                                viewModel.selectedMarkerState.value = markerState
                                false
                            }
                        )
                        {
                            Card(
                                modifier = Modifier.padding(4.dp),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    if (isNearest) {
                                        Text("⭐ Closest Point")
                                    }
                                    Text(p.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${p.cause} • ${p.schedule}", style = MaterialTheme.typography.bodySmall)
                                    if (p.accessible) {
                                        Text("Accessible", style = MaterialTheme.typography.bodySmall)
                                    }
                                    else{Text("Not Accessible", style = MaterialTheme.typography.bodySmall)}
                                }
                            }
                        }
                    }
                }

            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Location permission is required to use the map.")
                }
            }

            Card(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(0.95f)
                    .align(Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Row(Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterDropdown(
                        label = "Cause",
                        value = viewModel.cause.value,
                        items = listOf("All", "Children", "Adults", "Emergency"),
                        modifier = Modifier.weight(1f)
                    ) { newValue ->
                        viewModel.cause.value = newValue
                        viewModel.updateFilters()
                    }
                    FilterDropdown(
                        label = "Access",
                        value = viewModel.access.value,
                        items = listOf("All", "Accessible", "Not accessible"),
                        modifier = Modifier.weight(1f)
                    ) { newValue ->
                        viewModel.access.value = newValue
                        viewModel.updateFilters()
                    }
                    FilterDropdown(
                        label = "Schedule",
                        value = viewModel.schedule.value,
                        items = listOf("All", "Morning", "Afternoon", "Night"),
                        modifier = Modifier.weight(1f)
                    ) { newValue ->
                        viewModel.schedule.value = newValue
                        viewModel.updateFilters()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    value: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    onChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.background(White)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier
                .background(White)
                .menuAnchor(),
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis ) },
                    onClick = {
                        onChanged(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


