package com.hellmod.sailplanner.domain.repository

import com.hellmod.sailplanner.domain.model.Watch
import com.hellmod.sailplanner.domain.model.WatchLogEntry
import kotlinx.coroutines.flow.Flow

interface WatchRepository {
    fun observeWatches(tripId: String): Flow<List<Watch>>
    fun observeWatch(watchId: String): Flow<Watch?>
    fun observeCurrentWatch(tripId: String): Flow<Watch?>
    suspend fun createWatch(watch: Watch): Result<Watch>
    suspend fun updateWatch(watch: Watch): Result<Watch>
    suspend fun deleteWatch(watchId: String): Result<Unit>
    suspend fun addLogEntry(entry: WatchLogEntry): Result<WatchLogEntry>
    suspend fun updateLogEntry(entry: WatchLogEntry): Result<WatchLogEntry>
}
