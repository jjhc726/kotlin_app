package com.example.vistaquickdonation.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.vistaquickdonation.model.DonationPoint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay

class DonationMapViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Estados observables
    val userLocation = mutableStateOf<LatLng?>(null)
    val hasLocationPermission = mutableStateOf(false)
    val cause = mutableStateOf("All")
    val access = mutableStateOf("All")
    val schedule = mutableStateOf("All")

    val currentnearestPoint = mutableStateOf<DonationPoint?>(null)

    val selectedMarkerState = mutableStateOf<MarkerState?>(null)

    // Puntos de donación simulados
    private val points = listOf(
        DonationPoint("p1", "Fundación Niñez Feliz", LatLng(4.651, -74.060), "Children", true, "Morning"),
        DonationPoint("p2", "Abrigo para Todos", LatLng(4.730, -74.082), "Adults", false, "Afternoon"),
        DonationPoint("p3", "Refugio Esperanza", LatLng(4.705, -74.100), "Emergency", true, "Night"),
        DonationPoint("p4", "Ropero Comunitario", LatLng(4.745, -74.050), "Children", false, "Morning"),
    )

    val visiblePoints = mutableStateOf(points)

    init {
        checkPermission()
    }
    fun checkPermission(): Boolean {
        val granted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission.value = granted
        return granted
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocation() {
        if (!hasLocationPermission.value) return
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            loc?.let {
                userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    fun updateFilters() {
        visiblePoints.value = points.filter { p ->
            (cause.value == "All" || p.cause == cause.value) &&
                    (access.value == "All" || (access.value == "Accessible") == p.accessible) &&
                    (schedule.value == "All" || p.schedule == schedule.value)
        }
    }

    suspend fun findAndShowClosestPoint(markerStates: Map<String, MarkerState>) {
        if (visiblePoints.value.isNotEmpty() && userLocation.value != null) {
            val nearestPoint = visiblePoints.value.minByOrNull { p ->
                val userLoc = userLocation.value!!
                val dx = userLoc.latitude - p.position.latitude
                val dy = userLoc.longitude - p.position.longitude
                dx * dx + dy * dy
            }
            currentnearestPoint.value = nearestPoint

            delay(200)

            nearestPoint?.let {
                selectedMarkerState.value = markerStates[it.id]
            }
        }
    }
}
