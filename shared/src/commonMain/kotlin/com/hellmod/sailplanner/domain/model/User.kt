package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: String,
    val name: String,
    val email: String?,
    val avatarUrl: String?,
    val authProvider: AuthProvider,
    val createdAt: Instant
)

enum class AuthProvider {
    GOOGLE,
    APPLE,
    GUEST
}
