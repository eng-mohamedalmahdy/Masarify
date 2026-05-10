package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.sync.SyncPullResponse
import tech.lightfeather.domain.model.sync.SyncQueueEntry

interface SyncRepository {
    suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit>

    suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse>

    suspend fun downloadAttachment(remoteId: Long): DomainResult<ByteArray>
}
