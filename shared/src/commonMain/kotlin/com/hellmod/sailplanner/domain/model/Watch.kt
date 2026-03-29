package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

/**
 * Sailing watch (wachta) - a scheduled period when crew members are on duty.
 */
data class Watch(
    val id: String,
    val tripId: String,
    val name: String,
    val startTime: Instant,
    val endTime: Instant,
    val crewMembers: List<WatchCrew>,
    val notes: String?,
    val weatherCondition: WeatherCondition?,
    val logEntries: List<WatchLogEntry>
)

data class WatchCrew(
    val userId: String,
    val name: String,
    val role: WatchRole
)

enum class WatchRole {
    HELMSMAN,
    NAVIGATOR,
    LOOKOUT,
    CREW
}

data class WatchLogEntry(
    val id: String,
    val watchId: String,
    val timestamp: Instant,
    val position: GeoPoint?,
    val heading: Float?,
    val speed: Float?,
    val windSpeed: Float?,
    val windDirection: Float?,
    val note: String,
    val loggedBy: String
)

data class WeatherCondition(
    val windSpeed: Float,
    val windDirection: Float,
    val waveHeight: Float?,
    val visibility: Float?,
    val cloudCover: Int?,
    val temperature: Float?
)

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null
)
