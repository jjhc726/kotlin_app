@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vistaquickdonation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    // Filtros
    var cause by remember { mutableStateOf("Todos") }
    var access by remember { mutableStateOf("Todos") }
    var schedule by remember { mutableStateOf("Todos") }

    // Lista de puntos
    val points = listOf(
        DonationPoint("p1","Fundación Niñez Feliz",LatLng(4.651, -74.060),"Niñez",true,"Mañana"),
        DonationPoint("p2","Abrigo para Todos",LatLng(4.730, -74.082),"Adultos",false,"Tarde"),
        DonationPoint("p3","Refugio Esperanza",LatLng(4.705, -74.100),"Emergencia",true,"Noche"),
        DonationPoint("p4","Ropero Comunitario",LatLng(4.745, -74.050),"Niñez",false,"Mañana"),
    )

    // Estado del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    // Estado de ubicación del usuario
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Control de permisos
    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    // Pedir permiso si no lo tenemos
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

    Box(Modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            // Mapa
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true)
            ) {
                // Marcadores filtrados
                points.filter { p ->
                    (cause == "Todos" || p.cause == cause) &&
                            (access == "Todos" || (access == "Accesible") == p.accessible) &&
                            (schedule == "Todos" || p.schedule == schedule)
                }.forEach { p ->
                    Marker(
                        state = MarkerState(position = p.position),
                        title = p.name,
                        snippet = "${p.cause} • ${p.schedule}${if (p.accessible) " • ♿" else ""}"
                    )
                }
            }
        } else {
            Text(
                "Se necesita permiso de ubicación para mostrar el mapa",
                modifier = Modifier.padding(16.dp)
            )
        }

        // Filtros arriba
        Card(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .align(androidx.compose.ui.Alignment.TopCenter),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterDropdown(
                    label = "Causa",
                    value = cause,
                    items = listOf("Todos", "Niñez", "Adultos", "Emergencia"),
                    modifier = Modifier.weight(1f)
                ) { cause = it }

                FilterDropdown(
                    label = "Acceso",
                    value = access,
                    items = listOf("Todos", "Accesible", "No accesible"),
                    modifier = Modifier.weight(1f)
                ) { access = it }

                FilterDropdown(
                    label = "Horario",
                    value = schedule,
                    items = listOf("Todos", "Mañana", "Tarde", "Noche"),
                    modifier = Modifier.weight(1f)
                ) { schedule = it }
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
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onChanged(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

