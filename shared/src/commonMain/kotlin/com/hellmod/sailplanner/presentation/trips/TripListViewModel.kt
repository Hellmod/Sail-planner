package com.hellmod.sailplanner.presentation.trips

import com.hellmod.sailplanner.domain.model.Trip
import com.hellmod.sailplanner.domain.model.TripStatus
import com.hellmod.sailplanner.domain.repository.TripRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch

// ── State ──────────────────────────────────────────────────────────────────
data class TripListState(
    val isLoading: Boolean = true,
    val trips: List<Trip> = emptyList(),
    val activeTrip: Trip? = null,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface TripListIntent : Intent {
    data object LoadTrips : TripListIntent
    data class OpenTrip(val tripId: String) : TripListIntent
    data object CreateTrip : TripListIntent
    data class JoinTrip(val inviteCode: String) : TripListIntent
    data class DeleteTrip(val tripId: String) : TripListIntent
    data class ArchiveTrip(val tripId: String) : TripListIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface TripListEffect : Effect {
    data class NavigateToTrip(val tripId: String) : TripListEffect
    data object NavigateToCreateTrip : TripListEffect
    data class ShowError(val message: String) : TripListEffect
    data class ShowSuccess(val message: String) : TripListEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class TripListViewModel(
    private val tripRepository: TripRepository
) : BaseViewModel<TripListState, TripListIntent, TripListEffect>(TripListState()) {

    init {
        dispatch(TripListIntent.LoadTrips)
    }

    override suspend fun handleIntent(intent: TripListIntent) {
        when (intent) {
            TripListIntent.LoadTrips -> loadTrips()
            is TripListIntent.OpenTrip -> emitEffect(TripListEffect.NavigateToTrip(intent.tripId))
            TripListIntent.CreateTrip -> emitEffect(TripListEffect.NavigateToCreateTrip)
            is TripListIntent.JoinTrip -> joinTrip(intent.inviteCode)
            is TripListIntent.DeleteTrip -> deleteTrip(intent.tripId)
            is TripListIntent.ArchiveTrip -> archiveTrip(intent.tripId)
        }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            tripRepository.observeTrips().collect { trips ->
                updateState {
                    copy(
                        isLoading = false,
                        trips = trips,
                        activeTrip = trips.firstOrNull { it.status == TripStatus.ACTIVE }
                    )
                }
            }
        }
    }

    private suspend fun joinTrip(inviteCode: String) {
        updateState { copy(isLoading = true) }
        tripRepository.joinTrip(inviteCode)
            .onSuccess { trip ->
                updateState { copy(isLoading = false) }
                emitEffect(TripListEffect.ShowSuccess("Joined trip: ${trip.name}"))
                emitEffect(TripListEffect.NavigateToTrip(trip.id))
            }
            .onFailure { e ->
                updateState { copy(isLoading = false, error = e.message) }
                emitEffect(TripListEffect.ShowError(e.message ?: "Failed to join trip"))
            }
    }

    private suspend fun deleteTrip(tripId: String) {
        tripRepository.deleteTrip(tripId)
            .onSuccess { emitEffect(TripListEffect.ShowSuccess("Trip deleted")) }
            .onFailure { e -> emitEffect(TripListEffect.ShowError(e.message ?: "Failed to delete")) }
    }

    private suspend fun archiveTrip(tripId: String) {
        tripRepository.updateTripStatus(tripId, TripStatus.ARCHIVED)
            .onSuccess { emitEffect(TripListEffect.ShowSuccess("Trip archived")) }
            .onFailure { e -> emitEffect(TripListEffect.ShowError(e.message ?: "Failed to archive")) }
    }
}
