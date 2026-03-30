package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.AuthProvider
import com.hellmod.sailplanner.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithApple(idToken: String, nonce: String): Result<User>
    suspend fun signInAsGuest(): Result<User>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
    fun isSignedIn(): Boolean
}
