package com.hellmod.sailplanner.presentation.watch

import com.hellmod.sailplanner.domain.model.Watch
import com.hellmod.sailplanner.domain.model.WatchLogEntry
import com.hellmod.sailplanner.domain.repository.WatchRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State
import kotlinx.coroutines.launch

// ── State ──────────────────────────────────────────────────────────────────
data class WatchState(
    val isLoading: Boolean = true,
    val watches: List<Watch> = emptyList(),
    val currentWatch: Watch? = null,
    val selectedWatch: Watch? = null,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface WatchIntent : Intent {
    data class LoadWatches(val tripId: String) : WatchIntent
    data class SelectWatch(val watchId: String) : WatchIntent
    data class CreateWatch(val watch: Watch) : WatchIntent
    data class UpdateWatch(val watch: Watch) : WatchIntent
    data class DeleteWatch(val watchId: String) : WatchIntent
    data class AddLogEntry(val entry: WatchLogEntry) : WatchIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface WatchEffect : Effect {
    data class ShowError(val message: String) : WatchEffect
    data class ShowSuccess(val message: String) : WatchEffect
    data object ShowCreateWatchDialog : WatchEffect
    data object ShowAddLogEntryDialog : WatchEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class WatchViewModel(
    private val watchRepository: WatchRepository
) : BaseViewModel<WatchState, WatchIntent, WatchEffect>(WatchState()) {

    override suspend fun handleIntent(intent: WatchIntent) {
        when (intent) {
            is WatchIntent.LoadWatches -> loadWatches(intent.tripId)
            is WatchIntent.SelectWatch -> selectWatch(intent.watchId)
            is WatchIntent.CreateWatch -> createWatch(intent.watch)
            is WatchIntent.UpdateWatch -> updateWatch(intent.watch)
            is WatchIntent.DeleteWatch -> deleteWatch(intent.watchId)
            is WatchIntent.AddLogEntry -> addLogEntry(intent.entry)
        }
    }

    private fun loadWatches(tripId: String) {
        viewModelScope.launch {
            watchRepository.observeWatches(tripId).collect { watches ->
                updateState { copy(isLoading = false, watches = watches) }
            }
        }
        viewModelScope.launch {
            watchRepository.observeCurrentWatch(tripId).collect { watch ->
                updateState { copy(currentWatch = watch) }
            }
        }
    }

    private fun selectWatch(watchId: String) {
        viewModelScope.launch {
            watchRepository.observeWatch(watchId).collect { watch ->
                updateState { copy(selectedWatch = watch) }
            }
        }
    }

    private suspend fun createWatch(watch: Watch) {
        watchRepository.createWatch(watch)
            .onSuccess { emitEffect(WatchEffect.ShowSuccess("Watch created")) }
            .onFailure { e -> emitEffect(WatchEffect.ShowError(e.message ?: "Failed to create watch")) }
    }

    private suspend fun updateWatch(watch: Watch) {
        watchRepository.updateWatch(watch)
            .onFailure { e -> emitEffect(WatchEffect.ShowError(e.message ?: "Failed to update watch")) }
    }

    private suspend fun deleteWatch(watchId: String) {
        watchRepository.deleteWatch(watchId)
            .onSuccess { emitEffect(WatchEffect.ShowSuccess("Watch deleted")) }
            .onFailure { e -> emitEffect(WatchEffect.ShowError(e.message ?: "Failed to delete watch")) }
    }

    private suspend fun addLogEntry(entry: WatchLogEntry) {
        watchRepository.addLogEntry(entry)
            .onFailure { e -> emitEffect(WatchEffect.ShowError(e.message ?: "Failed to add log entry")) }
    }
}
