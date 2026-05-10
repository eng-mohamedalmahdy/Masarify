package tech.lightfeather.data.repository

import tech.lightfeather.data.remote.SyncApi
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.sync.SyncPullResponse
import tech.lightfeather.domain.model.sync.SyncQueueEntry
import tech.lightfeather.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val syncApi: SyncApi,
) : SyncRepository {
    override suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit> = syncApi.enqueue(entry)

    override suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse> = syncApi.pullDelta(since)

    override suspend fun downloadAttachment(remoteId: Long): DomainResult<ByteArray> =
        syncApi.downloadAttachment(remoteId)
}
