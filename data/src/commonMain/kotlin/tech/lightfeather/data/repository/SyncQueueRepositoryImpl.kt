package tech.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import io.github.aakira.napier.Napier
import tech.lightfeather.data.local.database.drivers.SharedDatabase
import tech.lightfeather.domain.model.sync.SyncQueueEntry
import tech.lightfeather.domain.repository.SyncQueueRepository
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

    override suspend fun markSent(
        id: Long,
        remoteId: Long?,
    ) {
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
            Napier.e("Error deleting sync queue entry", e)
        }
    }

    override suspend fun getFailedEntries(): List<SyncQueueEntry> =
        try {
            database {
                it.syncQueueQueries.getFailedEntries().awaitAsList().map { row ->
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
            Napier.e("Error getting failed sync entries", e)
            emptyList()
        }

    override suspend fun getFailedCount(): Long =
        try {
            database { it.syncQueueQueries.getFailedCount().awaitAsOne() }
        } catch (e: Exception) {
            Napier.e("Error getting failed sync count", e)
            0L
        }

    override suspend fun resetToRetry(id: Long) {
        try {
            database { it.syncQueueQueries.resetToRetry(id) }
        } catch (e: Exception) {
            Napier.e("Error resetting sync entry to retry", e)
        }
    }

    override suspend fun deleteAllFailed() {
        try {
            database { it.syncQueueQueries.deleteAllFailed() }
        } catch (e: Exception) {
            Napier.e("Error deleting all failed sync entries", e)
        }
    }

    override suspend fun hasActiveEntry(
        localId: Long,
        entityType: String,
    ): Boolean =
        try {
            database {
                it.syncQueueQueries.hasActiveEntry(localId = localId, entityType = entityType).awaitAsOne() > 0
            }
        } catch (e: Exception) {
            Napier.e("Error checking active sync queue entry", e)
            false
        }
}
