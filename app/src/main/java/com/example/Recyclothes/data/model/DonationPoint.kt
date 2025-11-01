package com.example.Recyclothes.data.model

import com.google.android.gms.maps.model.LatLng

data class DonationPoint(
    val id: String,
    val name: String,
    val position: LatLng,
    val cause: String,
    val accessible: Boolean,
    val schedule: String
)