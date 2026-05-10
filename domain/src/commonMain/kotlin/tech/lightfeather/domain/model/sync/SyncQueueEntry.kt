package tech.lightfeather.domain.model.sync

data class SyncQueueEntry(
    val id: Long,
    val entityType: String,
    val operation: String,
    val payload: String,
    val localId: Long?,
    val remoteId: Long?,
    val createdAt: Long,
    val retryCount: Int,
    val status: String,
)
