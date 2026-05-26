package tech.lightfeather.domain.fake

import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.SyncEnqueueHelper
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase

fun noSyncHelper(
    accountRepo: FakeAccountRepository = FakeAccountRepository(),
    txRepo: FakeTransactionRepository = FakeTransactionRepository(),
    categoryRepo: FakeCategoryRepository = FakeCategoryRepository(),
    syncQueueRepo: FakeSyncQueueRepository = FakeSyncQueueRepository(),
): SyncEnqueueHelper {
    val userRepo = FakeUserRepository()
    val syncRepo = FakeSyncRepository()
    val drain = DrainOutboxQueueUseCase(syncQueueRepo, syncRepo)
    val pull =
        PullRemoteDeltaUseCase(
            syncRepo,
            accountRepo,
            txRepo,
            categoryRepo,
            FakeCurrencyRepository(),
            FakeFinancialSessionRepository(),
            FakeAttachmentRepository(),
            userRepo,
        )
    val upload =
        UploadLocalDataUseCase(
            accountRepo,
            txRepo,
            categoryRepo,
            FakeCurrencyRepository(),
            FakeFinancialSessionRepository(),
            syncQueueRepo,
        )
    return SyncEnqueueHelper(syncQueueRepo, userRepo, drain, pull, upload)
}
