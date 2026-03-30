package com.hellmod.sailplanner.presentation.route

import com.hellmod.sailplanner.domain.model.GeoPoint
import com.hellmod.sailplanner.domain.model.RoutePoint
import com.hellmod.sailplanner.domain.repository.TripRepository
import com.hellmod.sailplanner.domain.service.LocationService
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

// ── State ──────────────────────────────────────────────────────────────────
data class RouteState(
    val isLoading: Boolean = true,
    val routePoints: List<RoutePoint> = emptyList(),
    val currentLocation: GeoPoint? = null,
    val isTracking: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val trackingIntervalSeconds: Long = 30,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface RouteIntent : Intent {
    data class LoadRoute(val tripId: String) : RouteIntent
    data class StartTracking(val tripId: String) : RouteIntent
    data class StopTracking(val tripId: String) : RouteIntent
    data object RequestLocationPermission : RouteIntent
    data class SetTrackingInterval(val seconds: Long) : RouteIntent
    data class AddManualPoint(val tripId: String, val location: GeoPoint) : RouteIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface RouteEffect : Effect {
    data class ShowError(val message: String) : RouteEffect
    data class ShowSuccess(val message: String) : RouteEffect
    data object RequestPermission : RouteEffect
    data class CenterMapOn(val location: GeoPoint) : RouteEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class RouteViewModel(
    private val tripRepository: TripRepository,
    private val locationService: LocationService
) : BaseViewModel<RouteState, RouteIntent, RouteEffect>(RouteState()) {

    init {
        observeTrackingState()
        checkPermission()
    }

    override suspend fun handleIntent(intent: RouteIntent) {
        when (intent) {
            is RouteIntent.LoadRoute -> loadRoute(intent.tripId)
            is RouteIntent.StartTracking -> startTracking(intent.tripId)
            is RouteIntent.StopTracking -> stopTracking(intent.tripId)
            RouteIntent.RequestLocationPermission -> requestPermission()
            is RouteIntent.SetTrackingInterval -> updateState { copy(trackingIntervalSeconds = intent.seconds) }
            is RouteIntent.AddManualPoint -> addManualPoint(intent.tripId, intent.location)
        }
    }

    private fun observeTrackingState() {
        viewModelScope.launch {
            locationService.isTracking.collect { tracking ->
                updateState { copy(isTracking = tracking) }
            }
        }
    }

    private fun checkPermission() {
        updateState { copy(hasLocationPermission = locationService.hasLocationPermission()) }
    }

    private fun loadRoute(tripId: String) {
        viewModelScope.launch {
            tripRepository.observeRoute(tripId).collect { points ->
                updateState { copy(isLoading = false, routePoints = points) }
            }
        }
        viewModelScope.launch {
            locationService.observeLocation().collect { location ->
                updateState { copy(currentLocation = location) }
            }
        }
    }

    private fun startTracking(tripId: String) {
        if (!state.value.hasLocationPermission) {
            emitEffect(RouteEffect.RequestPermission)
            return
        }
        locationService.startTracking(tripId, state.value.trackingIntervalSeconds)
        emitEffect(RouteEffect.ShowSuccess("Location tracking started"))
    }

    private fun stopTracking(tripId: String) {
        locationService.stopTracking()
        emitEffect(RouteEffect.ShowSuccess("Location tracking stopped"))
    }

    private suspend fun requestPermission() {
        val granted = locationService.requestLocationPermission()
        updateState { copy(hasLocationPermission = granted) }
        if (!granted) emitEffect(RouteEffect.ShowError("Location permission is required for tracking"))
    }

    private suspend fun addManualPoint(tripId: String, location: GeoPoint) {
        val point = RoutePoint(
            location = location,
            timestamp = Clock.System.now(),
            speed = null,
            heading = null
        )
        tripRepository.addRoutePoint(tripId, point)
            .onFailure { e -> emitEffect(RouteEffect.ShowError(e.message ?: "Failed to add point")) }
    }
}
