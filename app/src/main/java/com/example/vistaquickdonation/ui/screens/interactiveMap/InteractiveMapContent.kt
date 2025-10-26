package com.example.vistaquickdonation.ui.screens.interactiveMap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.White
import com.example.vistaquickdonation.viewmodel.DonationMapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

@Composable
fun InteractiveMapContent(
    viewModel: DonationMapViewModel,
    hasPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    val userLocation by viewModel.userLocation
    val visiblePoints by viewModel.visiblePoints
    val selectedMarkerState by viewModel.selectedMarkerState
    val currentnearestPoint by viewModel.currentnearestPoint

    val markerStates = remember { mutableMapOf<String, MarkerState>() }
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation, visiblePoints, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        delay(200)
        viewModel.findAndShowClosestPoint(markerStates)
    }

    LaunchedEffect(selectedMarkerState) {
        selectedMarkerState?.showInfoWindow()
    }

    Box(modifier.fillMaxSize()) {
        if (hasPermission) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true),
                onMapLoaded = { isMapLoaded = true },
            ) {
                visiblePoints.forEach { p ->
                    val markerState = remember(p.id) { MarkerState(position = p.position) }
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
                    ) {
                        Card(
                            modifier = Modifier.padding(4.dp),
                            colors = CardDefaults.cardColors(containerColor = White)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                if (isNearest) Text("⭐ Closest Point")
                                Text(p.name, style = MaterialTheme.typography.titleMedium)
                                Text("${p.cause} • ${p.schedule}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    if (p.accessible) "Accessible" else "Not Accessible",
                                    style = MaterialTheme.typography.bodySmall
                                )
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

        MapFilters(viewModel)
    }
}
