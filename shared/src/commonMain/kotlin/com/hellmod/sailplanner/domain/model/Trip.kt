package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

data class Trip(
    val id: String,
    val name: String,
    val description: String,
    val startPort: Port,
    val endPort: Port?,
    val startDate: Instant,
    val endDate: Instant?,
    val members: List<TripMember>,
    val status: TripStatus,
    val coverImageUrl: String?,
    val createdBy: String
)

data class Port(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class TripMember(
    val userId: String,
    val name: String,
    val avatarUrl: String?,
    val role: MemberRole
)

enum class MemberRole {
    CAPTAIN,
    CREW,
    GUEST
}

enum class TripStatus {
    PLANNED,
    ACTIVE,
    COMPLETED,
    ARCHIVED
}
