package com.hellmod.sailplanner.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.hellmod.sailplanner.domain.model.GeoPoint
import com.hellmod.sailplanner.domain.service.LocationService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidLocationService(
    private val context: Context
) : LocationService {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _isTracking = MutableStateFlow(false)
    override val isTracking: Flow<Boolean> = _isTracking

    override fun observeLocation(intervalSeconds: Long): Flow<GeoPoint> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            intervalSeconds * 1000L
        ).setMinUpdateIntervalMillis(intervalSeconds * 1000L / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(GeoPoint(loc.latitude, loc.longitude, loc.altitude, loc.accuracy))
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }

    override suspend fun getCurrentLocation(): Result<GeoPoint> =
        suspendCancellableCoroutine { cont ->
            if (!hasLocationPermission()) {
                cont.resume(Result.failure(SecurityException("Location permission not granted")))
                return@suspendCancellableCoroutine
            }
            fusedClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(Result.success(GeoPoint(loc.latitude, loc.longitude, loc.altitude)))
                    } else {
                        cont.resume(Result.failure(Exception("Location unavailable")))
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestLocationPermission(): Boolean {
        // Permission request is handled by the Activity via ActivityResultContracts
        // Return current status; actual request is triggered from the UI layer
        return hasLocationPermission()
    }

    override fun startTracking(tripId: String, intervalSeconds: Long) {
        Napier.i("Starting location tracking for trip $tripId at ${intervalSeconds}s interval")
        _isTracking.value = true
        // TODO: Start LocationTrackingService foreground service with tripId
    }

    override fun stopTracking() {
        Napier.i("Stopping location tracking")
        _isTracking.value = false
        // TODO: Stop LocationTrackingService
    }
}
