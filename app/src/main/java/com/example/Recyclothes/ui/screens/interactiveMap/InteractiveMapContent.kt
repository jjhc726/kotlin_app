package com.example.Recyclothes.ui.screens.interactiveMap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Recyclothes.data.repository.MapTelemetryRepository
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.White
import com.example.Recyclothes.viewmodel.DonationMapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@Composable
fun InteractiveMapContent(
    viewModel: DonationMapViewModel,
    hasPermission: Boolean,
    modifier: Modifier = Modifier,
    mapEventId: String
) {
    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 12f)
    }

    val visiblePoints by viewModel.visiblePoints
    val selectedMarkerState by viewModel.selectedMarkerState
    val currentnearestPoint by viewModel.currentnearestPoint
    val isMapBlocked by viewModel.isMapBlocked
    val hasNetwork by viewModel.hasNetwork
    val userLocation by viewModel.userLocation
    val mapTelemetryRepo = remember { MapTelemetryRepository() }
    val coroutineScope = rememberCoroutineScope()



    val markerStates = remember(visiblePoints) { mutableMapOf<String, MarkerState>() }
    var isMapLoaded by remember { mutableStateOf(false) }

    val showOfflineBanner = remember(hasNetwork, isMapBlocked, visiblePoints) {
        derivedStateOf {
            !hasNetwork && !isMapBlocked && visiblePoints.isNotEmpty()
        }
    }
    val bannerDismissed = remember { mutableStateOf(false) }

    LaunchedEffect(hasNetwork) {
        if (hasNetwork) bannerDismissed.value = false
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            viewModel.startLocationUpdates()
        } else {
            viewModel.stopLocationUpdates()
        }
        onDispose {
            viewModel.stopLocationUpdates()
        }
    }

    LaunchedEffect(userLocation, visiblePoints, isMapLoaded) {
        viewModel.findAndShowClosestPoint()

        markerStates[currentnearestPoint?.id]?.showInfoWindow()
    }


    LaunchedEffect(selectedMarkerState) {
        selectedMarkerState?.showInfoWindow()
    }


    Box(modifier.fillMaxSize()) {
        if (isMapBlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCCFFFFFF))
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Se necesita conexión para cargar el mapa por primera vez.",
                        color = DeepBlue,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshPoints() }) {
                        Text("Reintentar")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (!hasNetwork) "No hay conexión. Activa internet e intenta de nuevo." else "Intentando cargar...",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!isMapBlocked) {
            Box(modifier.fillMaxSize()) {
                if (hasPermission) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = true
                        ),
                        onMapLoaded = { coroutineScope.launch {
                            mapTelemetryRepo.endEvent(mapEventId)
                        }
                            isMapLoaded = true },

                    ) {
                        visiblePoints.forEach { p ->
                            val markerState = remember(p.id) { MarkerState(position = p.position) }
                            markerStates[p.id] = markerState
                            val isNearest = currentnearestPoint?.id == p.id

                            MarkerInfoWindow(
                                state = markerState,
                                title = p.name,
                                snippet = "${p.cause} • ${p.schedule}${if (p.accessible) " • Accessible" else " • Not Accessible"}",
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
                                        if (isNearest) Text("⭐ Closest Point", color = DeepBlue)
                                        Text(
                                            p.name,
                                            color = DeepBlue,
                                        )
                                        Text(
                                            "${p.cause} • ${p.schedule}",
                                            color = Color.Black
                                        )
                                        Text(
                                            if (p.accessible) "Accessible" else "Not Accessible",
                                            color = if (p.accessible) DeepBlue else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("Location permission is required to use the map.", color = DeepBlue)
                    }
                }

                MapFilters(viewModel)
                AnimatedVisibility(
                    visible = showOfflineBanner.value && !bannerDismissed.value,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
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
                                tint = DeepBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "You are offline. Showing locally saved donation points.",
                                color = DeepBlue,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
