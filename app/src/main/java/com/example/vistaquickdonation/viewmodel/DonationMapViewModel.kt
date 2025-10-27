package com.example.vistaquickdonation.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.vistaquickdonation.data.model.DonationPoint
import com.example.vistaquickdonation.data.repository.CharitiesRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope

class DonationMapViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = CharitiesRepository()
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

    val userLocation = mutableStateOf<LatLng?>(null)
    val hasLocationPermission = mutableStateOf(false)

    val cause = mutableStateOf("All")
    val access = mutableStateOf("All")
    val schedule = mutableStateOf("All")

    val currentnearestPoint = mutableStateOf<DonationPoint?>(null)
    val selectedMarkerState = mutableStateOf<MarkerState?>(null)

    // estados de UI
    private val allPoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val visiblePoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)


    init {
        checkPermission()
        refreshPoints()
    }

    fun checkPermission(): Boolean {
        val granted = ActivityCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
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

    fun refreshPoints() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val points = withContext(Dispatchers.IO) {
                    repository.getAllDonationPoints()
                }
                allPoints.value = points
                updateFilters()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = e.message ?: "Error loading donation points"
                visiblePoints.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun applyFilters(list: List<DonationPoint>): List<DonationPoint> {
        return list.filter { p ->
            val accessMatches = when (access.value) {
                "All" -> true
                "Accessible" -> p.accessible
                "Not accessible" -> !p.accessible
                else -> true
            }

            (cause.value == "All" || p.cause == cause.value) &&
                    accessMatches &&
                    (schedule.value == "All" || p.schedule == schedule.value)
        }
    }

    fun findAndShowClosestPoint() {
        val userLoc = userLocation.value
        val points = visiblePoints.value
        if (userLoc == null || points.isEmpty()) {
            currentnearestPoint.value = null
            return
        }

        val nearest = points.minByOrNull { p ->
            val dx = userLoc.latitude - p.position.latitude
            val dy = userLoc.longitude - p.position.longitude
            dx * dx + dy * dy
        }
        currentnearestPoint.value = nearest
    }

    fun updateFilters() {
        visiblePoints.value = applyFilters(allPoints.value)
        // recalculamos inmediatamente cuando cambian los filtros
        findAndShowClosestPoint()
    }
}
