package com.hellmod.sailplanner.domain.service

import com.hellmod.sailplanner.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic location service.
 * Implementations differ per platform (Android / iOS).
 *
 * Tracking interval is kept at 30-60 seconds to conserve battery while
 * maintaining enough resolution to draw an accurate sailing route.
 */
interface LocationService {
    /** Emits the current location on the specified interval (seconds). */
    fun observeLocation(intervalSeconds: Long = 30): Flow<GeoPoint>

    /** One-shot current location request. */
    suspend fun getCurrentLocation(): Result<GeoPoint>

    /** Check whether location permissions are granted. */
    fun hasLocationPermission(): Boolean

    /** Request location permissions from the OS. */
    suspend fun requestLocationPermission(): Boolean

    /** Start background location tracking and persist points to the local DB. */
    fun startTracking(tripId: String, intervalSeconds: Long = 30)

    /** Stop background tracking. */
    fun stopTracking()

    val isTracking: Flow<Boolean>
}
