package com.lightfeather.domain.repository

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.sync.SyncPullResponse
import com.lightfeather.domain.model.sync.SyncQueueEntry

interface SyncRepository {
    suspend fun enqueueRemote(entry: SyncQueueEntry): DomainResult<Unit>
    suspend fun pullDelta(since: Long): DomainResult<SyncPullResponse>
}
