package com.hellmod.sailplanner.presentation.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base MVI Store.
 *
 * S = State
 * I = Intent (user action / event from UI)
 * E = Effect (one-shot side-effect, e.g. navigation, snackbar)
 */
abstract class MviStore<S : State, I : Intent, E : Effect>(
    initialState: S
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun dispatch(intent: I) {
        scope.launch {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)

    protected fun updateState(transform: S.() -> S) {
        _state.update { it.transform() }
    }

    protected fun emitEffect(effect: E) {
        scope.launch {
            _effects.send(effect)
        }
    }

    open fun onCleared() {
        // override if needed to cancel jobs, close resources
    }
}

interface State
interface Intent
interface Effect
