package com.example.vistaquickdonation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkObserver(private val context: Context) {
    @SuppressLint("ServiceCast")
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isOnline(): Boolean {
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun registerCallback(onAvailable: () -> Unit, onLost: () -> Unit): ConnectivityManager.NetworkCallback {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        val cb = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { onAvailable() }
            override fun onLost(network: Network) { onLost() }
        }
        cm.registerNetworkCallback(request, cb)
        return cb
    }

    fun unregisterCallback(callback: ConnectivityManager.NetworkCallback) {
        try { cm.unregisterNetworkCallback(callback) } catch (_: Exception) {}
    }
}