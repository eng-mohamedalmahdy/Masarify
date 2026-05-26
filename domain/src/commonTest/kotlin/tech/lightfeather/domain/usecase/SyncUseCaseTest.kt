package tech.lightfeather.domain.usecase

import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeAccountRepository
import tech.lightfeather.domain.fake.FakeAttachmentRepository
import tech.lightfeather.domain.fake.FakeCategoryRepository
import tech.lightfeather.domain.fake.FakeCurrencyRepository
import tech.lightfeather.domain.fake.FakeFinancialSessionRepository
import tech.lightfeather.domain.fake.FakeSyncQueueRepository
import tech.lightfeather.domain.fake.FakeSyncRepository
import tech.lightfeather.domain.fake.FakeTransactionRepository
import tech.lightfeather.domain.fake.FakeUserRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SyncUseCaseTest {
    @Test
    fun drainEmptyQueueCompletesWithoutError() =
        runTest {
            val queueRepo = FakeSyncQueueRepository()
            val syncRepo = FakeSyncRepository()

            DrainOutboxQueueUseCase(queueRepo, syncRepo)()

            assertEquals(0, syncRepo.enqueuedCount)
        }

    @Test
    fun drainCallsEnqueueRemoteForEachPendingEntry() =
        runTest {
            val queueRepo = FakeSyncQueueRepository()
            queueRepo.addPending()
            queueRepo.addPending()
            val syncRepo = FakeSyncRepository()

            DrainOutboxQueueUseCase(queueRepo, syncRepo)()

            assertEquals(2, syncRepo.enqueuedCount)
        }

    @Test
    fun drainConflictErrorMarksEntryAsFailed() =
        runTest {
            val queueRepo = FakeSyncQueueRepository()
            val entry = queueRepo.addPending()
            val syncRepo = FakeSyncRepository(shouldConflict = true)

            DrainOutboxQueueUseCase(queueRepo, syncRepo)()

            assertTrue(queueRepo.markedFailedIds.contains(entry.id))
        }

    @Test
    fun pullEmptyDeltaUpdatesSyncTimestamp() =
        runTest {
            val userRepo = FakeUserRepository()
            val pullUseCase =
                PullRemoteDeltaUseCase(
                    syncRepository = FakeSyncRepository(),
                    accountRepository = FakeAccountRepository(),
                    transactionRepository = FakeTransactionRepository(),
                    categoryRepository = FakeCategoryRepository(),
                    currencyRepository = FakeCurrencyRepository(),
                    financialSessionRepository = FakeFinancialSessionRepository(),
                    attachmentRepository = FakeAttachmentRepository(),
                    userRepository = userRepo,
                )

            pullUseCase()

            assertNotNull(userRepo.lastSyncAtSet)
        }

    @Test
    fun uploadWithNoUnsyncedDataDoesNotEnqueueAnything() =
        runTest {
            val queueRepo = FakeSyncQueueRepository()
            val uploadUseCase =
                UploadLocalDataUseCase(
                    accountRepository = FakeAccountRepository(),
                    transactionRepository = FakeTransactionRepository(),
                    categoryRepository = FakeCategoryRepository(),
                    currencyRepository = FakeCurrencyRepository(),
                    financialSessionRepository = FakeFinancialSessionRepository(),
                    syncQueueRepository = queueRepo,
                )

            uploadUseCase()

            assertEquals(0, queueRepo.pending.size)
        }
}
