package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.RoutePoint
import com.hellmod.sailplanner.domain.model.Trip
import com.hellmod.sailplanner.domain.model.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>
    fun observeTrip(tripId: String): Flow<Trip?>
    suspend fun createTrip(trip: Trip): Result<Trip>
    suspend fun updateTrip(trip: Trip): Result<Trip>
    suspend fun deleteTrip(tripId: String): Result<Unit>
    suspend fun joinTrip(inviteCode: String): Result<Trip>
    suspend fun generateInviteCode(tripId: String): Result<String>
    suspend fun updateTripStatus(tripId: String, status: TripStatus): Result<Unit>
    suspend fun addRoutePoint(tripId: String, point: RoutePoint): Result<Unit>
    fun observeRoute(tripId: String): Flow<List<RoutePoint>>
}
