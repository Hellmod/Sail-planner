package com.hellmod.sailplanner.presentation.auth

import com.hellmod.sailplanner.domain.model.User
import com.hellmod.sailplanner.domain.repository.AuthRepository
import com.hellmod.sailplanner.presentation.mvi.BaseViewModel
import com.hellmod.sailplanner.presentation.mvi.Effect
import com.hellmod.sailplanner.presentation.mvi.Intent
import com.hellmod.sailplanner.presentation.mvi.State

// ── State ──────────────────────────────────────────────────────────────────
data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
) : State

// ── Intents ────────────────────────────────────────────────────────────────
sealed interface AuthIntent : Intent {
    data class SignInWithGoogle(val idToken: String) : AuthIntent
    data class SignInWithApple(val idToken: String, val nonce: String) : AuthIntent
    data object SignInAsGuest : AuthIntent
    data object SignOut : AuthIntent
    data object ClearError : AuthIntent
}

// ── Effects ────────────────────────────────────────────────────────────────
sealed interface AuthEffect : Effect {
    data object NavigateToDashboard : AuthEffect
    data object NavigateToAuth : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}

// ── ViewModel ──────────────────────────────────────────────────────────────
class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignInWithGoogle -> signInWithGoogle(intent.idToken)
            is AuthIntent.SignInWithApple -> signInWithApple(intent.idToken, intent.nonce)
            AuthIntent.SignInAsGuest -> signInAsGuest()
            AuthIntent.SignOut -> signOut()
            AuthIntent.ClearError -> updateState { copy(error = null) }
        }
    }

    private suspend fun signInWithGoogle(idToken: String) {
        updateState { copy(isLoading = true, error = null) }
        authRepository.signInWithGoogle(idToken)
            .onSuccess { user ->
                updateState { copy(isLoading = false, user = user) }
                emitEffect(AuthEffect.NavigateToDashboard)
            }
            .onFailure { e ->
                updateState { copy(isLoading = false, error = e.message) }
                emitEffect(AuthEffect.ShowError(e.message ?: "Google sign-in failed"))
            }
    }

    private suspend fun signInWithApple(idToken: String, nonce: String) {
        updateState { copy(isLoading = true, error = null) }
        authRepository.signInWithApple(idToken, nonce)
            .onSuccess { user ->
                updateState { copy(isLoading = false, user = user) }
                emitEffect(AuthEffect.NavigateToDashboard)
            }
            .onFailure { e ->
                updateState { copy(isLoading = false, error = e.message) }
                emitEffect(AuthEffect.ShowError(e.message ?: "Apple sign-in failed"))
            }
    }

    private suspend fun signInAsGuest() {
        updateState { copy(isLoading = true, error = null) }
        authRepository.signInAsGuest()
            .onSuccess { user ->
                updateState { copy(isLoading = false, user = user) }
                emitEffect(AuthEffect.NavigateToDashboard)
            }
            .onFailure { e ->
                updateState { copy(isLoading = false, error = e.message) }
                emitEffect(AuthEffect.ShowError(e.message ?: "Guest sign-in failed"))
            }
    }

    private suspend fun signOut() {
        authRepository.signOut()
        updateState { copy(user = null) }
        emitEffect(AuthEffect.NavigateToAuth)
    }
}
