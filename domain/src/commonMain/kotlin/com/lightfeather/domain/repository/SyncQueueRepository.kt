package com.lightfeather.domain.repository

import com.lightfeather.domain.model.sync.SyncQueueEntry

interface SyncQueueRepository {
    suspend fun insertEntry(
        entityType: String,
        operation: String,
        payload: String,
        localId: Long?,
        remoteId: Long?,
    )

    suspend fun getPendingEntries(): List<SyncQueueEntry>

    suspend fun markSent(id: Long, remoteId: Long?)

    suspend fun incrementRetry(id: Long)

    suspend fun markFailed(id: Long)

    suspend fun deleteById(id: Long)
}
