package com.example.vistaquickdonation.ui.screens.interactiveMap

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.viewmodel.DonationMapViewModel

@SuppressLint("MissingPermission")
@Composable
fun InteractiveMapScreen(viewModel: DonationMapViewModel = viewModel()) {
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

    InteractiveMapContent(
        viewModel = viewModel,
        hasPermission = hasPermission,
        modifier = Modifier
    )
}
