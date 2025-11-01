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
import com.example.vistaquickdonation.utils.NetworkObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import android.net.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.*


class DonationMapViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = CharitiesRepository(getApplication())
    private val networkObserver = NetworkObserver(getApplication())

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

    // existing states...
    val userLocation = mutableStateOf<LatLng?>(null)
    val hasLocationPermission = mutableStateOf(false)

    val cause = mutableStateOf("All")
    val access = mutableStateOf("All")
    val schedule = mutableStateOf("All")

    val currentnearestPoint = mutableStateOf<DonationPoint?>(null)
    val selectedMarkerState = mutableStateOf<MarkerState?>(null)

    // UI states
    private val allPoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val visiblePoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // NEW: network / map block states
    val hasNetwork = mutableStateOf(false)
    val isMapBlocked = mutableStateOf(false) // si true => overlay bloqueante por primera carga sin cache

    private var connectivityCallback: ConnectivityManager.NetworkCallback? = null

    init {
        checkPermission()
        initConnectivity()
        refreshPoints() // intentará remoto -> fallback local
    }

    private fun initConnectivity() {
        hasNetwork.value = networkObserver.isOnline()
        connectivityCallback = networkObserver.registerCallback(
            onAvailable = { viewModelScope.launch { onNetworkAvailable() } },
            onLost = { viewModelScope.launch { onNetworkLost() } }
        )
    }

    private suspend fun onNetworkAvailable() {
        hasNetwork.value = true
        // If map was blocked because there was no cache, try to refresh remote and unblock if succeeded
        refreshPoints()
    }

    private suspend fun onNetworkLost() {
        hasNetwork.value = false
        // If no local cache -> block map
        val local = withContext(Dispatchers.IO) { repository.getLocalDonationPoints() }
        isMapBlocked.value = local.isEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
        connectivityCallback?.let { networkObserver.unregisterCallback(it) }
    }

    // permission & location logic unchanged...
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

    // locationRequest & callback unchanged...
    // startLocationUpdates/stopLocationUpdates unchanged

    fun refreshPoints() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                // repository.getAllDonationPoints() tries remote then fallback local
                val points = withContext(Dispatchers.IO) {
                    repository.getAllDonationPoints()
                }
                allPoints.value = points
                // If we have no points and also no network => block map
                if (points.isEmpty() && !networkObserver.isOnline()) {
                    isMapBlocked.value = true
                } else {
                    isMapBlocked.value = false
                }
                updateFilters()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = e.message ?: "Error loading donation points"
                // fallback to local
                val local = withContext(Dispatchers.IO) { repository.getLocalDonationPoints() }
                allPoints.value = local
                isMapBlocked.value = local.isEmpty()
                updateFilters()
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
            // sigue usando dx/dy simple (rápido). Puedes sustituir por haversine si quieres precisión.
            val dx = userLoc.latitude - p.position.latitude
            val dy = userLoc.longitude - p.position.longitude
            dx * dx + dy * dy
        }
        currentnearestPoint.value = nearest
    }

    fun updateFilters() {
        visiblePoints.value = applyFilters(allPoints.value)
        findAndShowClosestPoint()
    }
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateDistanceMeters(5f)
        .setMinUpdateIntervalMillis(2000L)
        .setMaxUpdateDelayMillis(10000L)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val newLatLng = LatLng(loc.latitude, loc.longitude)
            val prev = userLocation.value
            if (prev == null || prev.latitude != newLatLng.latitude || prev.longitude != newLatLng.longitude) {
                userLocation.value = newLatLng
                findAndShowClosestPoint()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (!hasLocationPermission.value) return
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
