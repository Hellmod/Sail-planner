package com.hellmod.sailplanner.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel-based MVI base class for Compose Multiplatform.
 * Used on Android via standard ViewModel; on iOS via Decompose ComponentContext.
 */
abstract class BaseViewModel<S : State, I : Intent, E : Effect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun dispatch(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: I)

    protected fun updateState(transform: S.() -> S) {
        _state.update { it.transform() }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
