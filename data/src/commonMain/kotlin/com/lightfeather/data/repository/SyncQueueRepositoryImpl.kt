package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.sync.SyncQueueEntry
import com.lightfeather.domain.repository.SyncQueueRepository
import io.github.aakira.napier.Napier
import kotlin.time.Clock

// DomainResult pattern requires catching all exceptions for proper error handling
@Suppress("TooGenericExceptionCaught")
class SyncQueueRepositoryImpl(
    private val database: SharedDatabase,
) : SyncQueueRepository {
    override suspend fun insertEntry(
        entityType: String,
        operation: String,
        payload: String,
        localId: Long?,
        remoteId: Long?,
    ) {
        try {
            database {
                it.syncQueueQueries.insertEntry(
                    entityType = entityType,
                    operation = operation,
                    payload = payload,
                    localId = localId,
                    remoteId = remoteId,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                )
            }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
        }
    }

    override suspend fun getPendingEntries(): List<SyncQueueEntry> =
        try {
            database {
                it.syncQueueQueries.getPendingEntries().awaitAsList().map { row ->
                    SyncQueueEntry(
                        id = row.id,
                        entityType = row.entity_type,
                        operation = row.operation,
                        payload = row.payload,
                        localId = row.local_id,
                        remoteId = row.remote_id,
                        createdAt = row.created_at,
                        retryCount = row.retry_count.toInt(),
                        status = row.status,
                    )
                }
            }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
            emptyList()
        }

    override suspend fun markSent(id: Long, remoteId: Long?) {
        try {
            database { it.syncQueueQueries.markSent(remoteId = remoteId, id = id) }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
        }
    }

    override suspend fun incrementRetry(id: Long) {
        try {
            database { it.syncQueueQueries.incrementRetry(id) }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
        }
    }

    override suspend fun markFailed(id: Long) {
        try {
            database { it.syncQueueQueries.markFailed(id) }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
        }
    }

    override suspend fun deleteById(id: Long) {
        try {
            database { it.syncQueueQueries.deleteById(id) }
        } catch (e: Exception) {
            Napier.e("Error inserting sync queue entry", e)
        }
    }
}
