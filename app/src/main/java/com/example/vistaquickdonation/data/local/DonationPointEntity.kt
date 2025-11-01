package com.example.vistaquickdonation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vistaquickdonation.data.model.DonationPoint
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "donation_points")
data class DonationPointEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val cause: String,
    val schedule: String,
    val accessible: Boolean,
    val lastUpdated: Long
) {
    fun toDomain() = DonationPoint(
        id = id,
        name = name,
        position = LatLng(lat, lng),
        cause = cause,
        schedule = schedule,
        accessible = accessible
    )
}

fun DonationPoint.toEntity() = DonationPointEntity(
    id = id,
    name = name,
    lat = position.latitude,
    lng = position.longitude,
    cause = cause,
    schedule = schedule,
    accessible = accessible,
    lastUpdated = System.currentTimeMillis()
)