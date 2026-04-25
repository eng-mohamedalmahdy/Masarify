package com.lightfeather.data.repository

import com.lightfeather.data.remote.SyncApi
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.sync.SyncPullResponse
import com.lightfeather.domain.model.sync.SyncQueueEntry
import com.lightfeather.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val syncApi: SyncApi,
) : SyncRepository {
    override suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit> =
        syncApi.enqueue(entry)

    override suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse> =
        syncApi.pullDelta(since)
}
