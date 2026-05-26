package tech.lightfeather.domain.usecase

import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeSyncQueueRepository
import tech.lightfeather.domain.fake.FakeSyncRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncQueueUseCaseTest {
    @Test
    fun getFailedSyncEntriesReturnsAllFailedEntries() =
        runTest {
            val repo = FakeSyncQueueRepository()
            repo.addFailed()
            repo.addFailed()

            val entries = GetFailedSyncEntriesUseCase(repo)()

            assertEquals(2, entries.size)
        }

    @Test
    fun getFailedSyncCountReturnsCountOfFailedEntries() =
        runTest {
            val repo = FakeSyncQueueRepository()
            repo.addFailed()

            val count = GetFailedSyncCountUseCase(repo)()

            assertEquals(1L, count)
        }

    @Test
    fun retrySyncEntryResetsStatusToPending() =
        runTest {
            val repo = FakeSyncQueueRepository()
            val entry = repo.addFailed()

            RetrySyncEntryUseCase(repo)(entry.id)

            assertEquals("PENDING", repo.failed.first { it.id == entry.id }.status)
        }

    @Test
    fun deleteFailedSyncEntryRemovesEntryFromFailedList() =
        runTest {
            val repo = FakeSyncQueueRepository()
            val entry = repo.addFailed()

            DeleteFailedSyncEntryUseCase(repo)(entry.id)

            assertTrue(repo.failed.none { it.id == entry.id })
        }

    @Test
    fun deleteAllFailedClearsEntireFailedList() =
        runTest {
            val repo = FakeSyncQueueRepository()
            repo.addFailed()
            repo.addFailed()
            repo.addFailed()

            DeleteAllFailedSyncUseCase(repo)()

            assertTrue(repo.failed.isEmpty())
        }

    @Test
    fun retryAllFailedResetsAllEntriesThenDrainsQueue() =
        runTest {
            val repo = FakeSyncQueueRepository()
            repo.addFailed()
            repo.addFailed()
            val drain = DrainOutboxQueueUseCase(repo, FakeSyncRepository())

            RetryAllFailedSyncUseCase(repo, drain)()

            assertTrue(repo.failed.all { it.status == "PENDING" })
        }
}
