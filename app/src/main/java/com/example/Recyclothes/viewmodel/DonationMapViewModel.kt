package com.example.Recyclothes.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.Recyclothes.data.model.DonationPoint
import com.example.Recyclothes.data.repository.CharitiesRepository
import com.example.Recyclothes.utils.NetworkObserver
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
import android.util.LruCache
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope


class DonationMapViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = CharitiesRepository(getApplication())
    private val networkObserver = NetworkObserver(getApplication())

    private val filterCache = object : LruCache<String, List<DonationPoint>>(50) {}

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

    val userLocation = mutableStateOf<LatLng?>(null)
    val hasLocationPermission = mutableStateOf(false)

    val cause = mutableStateOf("All")
    val access = mutableStateOf("All")
    val schedule = mutableStateOf("All")

    val currentnearestPoint = mutableStateOf<DonationPoint?>(null)
    val selectedMarkerState = mutableStateOf<MarkerState?>(null)

    private val allPoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val visiblePoints = mutableStateOf<List<DonationPoint>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    val hasNetwork = mutableStateOf(false)
    val isMapBlocked = mutableStateOf(false)

    private var connectivityCallback: ConnectivityManager.NetworkCallback? = null

    init {
        checkPermission()
        initConnectivity()
        refreshPoints()
    }

    private fun initConnectivity() {
        hasNetwork.value = networkObserver.isOnline()
        connectivityCallback = networkObserver.registerCallback(
            onAvailable = { viewModelScope.launch { onNetworkAvailable() } },
            onLost = { viewModelScope.launch { onNetworkLost() } }
        )
    }

    private fun onNetworkAvailable() {
        hasNetwork.value = true
        refreshPoints()
    }

    private suspend fun onNetworkLost() {
        hasNetwork.value = false
        val local = withContext(Dispatchers.IO) { repository.getLocalDonationPoints() }
        isMapBlocked.value = local.isEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
        connectivityCallback?.let { networkObserver.unregisterCallback(it) }
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
            supervisorScope {
                val remoteDeferred = async(Dispatchers.IO) { repository.getRemoteAndCache() }
                val localDeferred = async(start = CoroutineStart.LAZY, context = Dispatchers.IO) {
                    repository.getLocalDonationPoints()
                }
                val points = try {
                    remoteDeferred.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    localDeferred.await()
                }

                allPoints.value = points
                isMapBlocked.value = points.isEmpty()
                updateFilters()
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
        val key = "${cause.value}|${access.value}|${schedule.value}"

        val cached = filterCache.get(key)
        if (cached != null) {
            visiblePoints.value = cached
            findAndShowClosestPoint()
            return
        }

        val filtered = applyFilters(allPoints.value)
        filterCache.put(key, filtered)

        visiblePoints.value = filtered
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
