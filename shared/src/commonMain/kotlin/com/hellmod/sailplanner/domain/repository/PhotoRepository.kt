package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.GeoPoint
import com.hellmod.sailplanner.domain.model.TripCollage
import com.hellmod.sailplanner.domain.model.TripPhoto
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun observePhotos(tripId: String): Flow<List<TripPhoto>>
    suspend fun uploadPhoto(
        tripId: String,
        imageBytes: ByteArray,
        location: GeoPoint?,
        caption: String?
    ): Result<TripPhoto>
    suspend fun deletePhoto(photoId: String): Result<Unit>
    suspend fun updatePhoto(photo: TripPhoto): Result<TripPhoto>
    suspend fun generateCollage(tripId: String): Result<TripCollage>
    suspend fun getCollage(tripId: String): Result<TripCollage?>
}
