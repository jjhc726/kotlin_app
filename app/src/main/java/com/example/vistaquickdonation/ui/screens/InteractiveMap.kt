@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vistaquickdonation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.vistaquickdonation.ui.theme.*

// Modelo de punto de donación
data class DonationPoint(
    val id: String,
    val name: String,
    val position: LatLng,
    val cause: String,
    val accessible: Boolean,
    val schedule: String
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationMapScreen() {
    val bogota = LatLng(4.7110, -74.0721)

    var cause by remember { mutableStateOf("All") }
    var access by remember { mutableStateOf("All") }
    var schedule by remember { mutableStateOf("All") }

    val points = listOf(
        DonationPoint("p1", "Fundación Niñez Feliz", LatLng(4.651, -74.060), "Children", true, "Morning"),
        DonationPoint("p2", "Abrigo para Todos", LatLng(4.730, -74.082), "Adults", false, "Afternoon"),
        DonationPoint("p3", "Refugio Esperanza", LatLng(4.705, -74.100), "Emergency", true, "Night"),
        DonationPoint("p4", "Ropero Comunitario", LatLng(4.745, -74.050), "Children", false, "Morning"),
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    val visiblePoints = remember(cause, access, schedule, points) {
        points.filter { p ->
            (cause == "All" || p.cause == cause) &&
                    (access == "All" || (access == "Accessible") == p.accessible) &&
                    (schedule == "All" || p.schedule == schedule)
        }
    }

    var selectedPoint by remember { mutableStateOf<DonationPoint?>(null) }

    val markerStates = remember { mutableMapOf<String, MarkerState>() }


    LaunchedEffect(userLocation, visiblePoints, isMapLoaded) {

        if (!isMapLoaded) return@LaunchedEffect

        userLocation?.let { userLoc ->
            val nearestPoint = visiblePoints.minByOrNull { p ->
                val dx = userLoc.latitude - p.position.latitude
                val dy = userLoc.longitude - p.position.longitude
                dx * dx + dy * dy
            }
            selectedPoint = nearestPoint

            kotlinx.coroutines.delay(100)

            nearestPoint?.let {
                markerStates[it.id]?.showInfoWindow()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Donation Map") },
                modifier = Modifier.height(100.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealDark,
                    titleContentColor = White,
                )
            )
        }
    ) { innerPadding ->

        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            if (hasLocationPermission) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    onMapLoaded = { isMapLoaded = true }
                ) {
                    visiblePoints.forEach { p ->
                        val markerState = remember { MarkerState(position = p.position) }
                        markerStates[p.id] = markerState
                        val isNearest = p == selectedPoint
                        MarkerInfoWindow(
                            state = markerState,
                            title = p.name,
                            snippet = "${p.cause} • ${p.schedule}${if (p.accessible) " • Accessible" else ""}",
                            onClick = {
                                selectedPoint = p
                                false // Devuelve false para que el comportamiento por defecto (mostrar info) ocurra
                            }
                        ) {
                            // Contenido personalizado del popup
                            Card(
                                modifier = Modifier.padding(4.dp),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    if (isNearest) {
                                        Text("⭐ Closest Point")
                                    }
                                    Text(p.name)
                                    Text("${p.cause} • ${p.schedule}")
                                    if (p.accessible) Text("Accessible")
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Se necesita permiso de ubicación para mostrar el mapa",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Filtros
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .background(White)
                    .fillMaxWidth()
                    .align(androidx.compose.ui.Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(White)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterDropdown(
                        label = "Cause",
                        value = cause,
                        items = listOf("All", "Children", "Adults", "Emergency"),
                        modifier = Modifier.weight(1f).background(White)
                    ) { cause = it }

                    FilterDropdown(
                        label = "Access",
                        value = access,
                        items = listOf("All", "Accessible", "Not accessible"),
                        modifier = Modifier.weight(1f).background(White)
                    ) { access = it }

                    FilterDropdown(
                        label = "Schedule",
                        value = schedule,
                        items = listOf("All", "Morning", "Afternoon", "Night"),
                        modifier = Modifier.weight(1f).background(White)
                    ) { schedule = it }
                }
            }
        }
    }
}
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
            label = {
                Text(
                    label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier
                .background(White)
                .menuAnchor(),
            maxLines = 1,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                     onClick = {
                        onChanged(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

