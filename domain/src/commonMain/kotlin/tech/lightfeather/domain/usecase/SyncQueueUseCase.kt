package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.sync.SyncQueueEntry
import tech.lightfeather.domain.repository.SyncQueueRepository

class GetFailedSyncEntriesUseCase(
    private val syncQueueRepository: SyncQueueRepository,
) {
    suspend operator fun invoke(): List<SyncQueueEntry> =
        syncQueueRepository.getFailedEntries()
}

class GetFailedSyncCountUseCase(
    private val syncQueueRepository: SyncQueueRepository,
) {
    suspend operator fun invoke(): Long =
        syncQueueRepository.getFailedCount()
}

class RetrySyncEntryUseCase(
    private val syncQueueRepository: SyncQueueRepository,
) {
    suspend operator fun invoke(id: Long) =
        syncQueueRepository.resetToRetry(id)
}

class RetryAllFailedSyncUseCase(
    private val syncQueueRepository: SyncQueueRepository,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
) {
    suspend operator fun invoke() {
        val failed = syncQueueRepository.getFailedEntries()
        for (entry in failed) {
            syncQueueRepository.resetToRetry(entry.id)
        }
        drainOutboxQueueUseCase()
    }
}

class DeleteFailedSyncEntryUseCase(
    private val syncQueueRepository: SyncQueueRepository,
) {
    suspend operator fun invoke(id: Long) =
        syncQueueRepository.deleteById(id)
}

class DeleteAllFailedSyncUseCase(
    private val syncQueueRepository: SyncQueueRepository,
) {
    suspend operator fun invoke() =
        syncQueueRepository.deleteAllFailed()
}
