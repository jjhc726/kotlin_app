package com.example.Recyclothes.data.repository

import com.example.Recyclothes.data.remote.MapLoadTelemetryService
import java.util.UUID

class MapTelemetryRepository(
    private val service: MapLoadTelemetryService = MapLoadTelemetryService()
) {

    fun generateEventId(): String = UUID.randomUUID().toString()

    suspend fun startEvent(eventId: String) {
        val now = System.currentTimeMillis()
        service.logMapLoadStart(eventId, now)
    }

    suspend fun endEvent(eventId: String) {
        val now = System.currentTimeMillis()
        service.logMapLoadEnd(eventId, now)
    }
}
