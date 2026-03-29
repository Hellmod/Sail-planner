package com.hellmod.sailplanner.domain.model

import kotlinx.datetime.Instant

data class TripPhoto(
    val id: String,
    val tripId: String,
    val uploadedBy: String,
    val imageUrl: String,
    val thumbnailUrl: String?,
    val location: GeoPoint?,
    val takenAt: Instant,
    val uploadedAt: Instant,
    val caption: String?,
    val tags: List<String>,
    val isIncludedInCollage: Boolean
)

data class TripCollage(
    val tripId: String,
    val routePoints: List<RoutePoint>,
    val photos: List<TripPhoto>,
    val ports: List<Port>,
    val generatedAt: Instant?,
    val collageImageUrl: String?
)

data class RoutePoint(
    val location: GeoPoint,
    val timestamp: Instant,
    val speed: Float?,
    val heading: Float?
)
