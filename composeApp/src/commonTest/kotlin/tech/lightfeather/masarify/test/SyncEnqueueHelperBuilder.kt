package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.SyncEnqueueHelper
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase

internal fun buildSyncEnqueueHelper(): SyncEnqueueHelper {
    val syncQueueRepo = FakeSyncQueueRepository()
    val syncRepo = FakeSyncRepository()
    val userRepo = FakeUserRepository()
    val accountRepo = FakeAccountRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val currencyRepo = FakeCurrencyRepository()
    val financialSessionRepo = FakeFinancialSessionRepository()
    val attachmentRepo = FakeAttachmentRepository()

    return SyncEnqueueHelper(
        syncQueueRepository = syncQueueRepo,
        userRepository = userRepo,
        drainOutboxQueueUseCase =
            DrainOutboxQueueUseCase(
                syncQueueRepository = syncQueueRepo,
                syncRepository = syncRepo,
            ),
        pullRemoteDeltaUseCase =
            PullRemoteDeltaUseCase(
                syncRepository = syncRepo,
                accountRepository = accountRepo,
                transactionRepository = transactionRepo,
                categoryRepository = categoryRepo,
                currencyRepository = currencyRepo,
                financialSessionRepository = financialSessionRepo,
                attachmentRepository = attachmentRepo,
                userRepository = userRepo,
            ),
        uploadLocalDataUseCase =
            UploadLocalDataUseCase(
                accountRepository = accountRepo,
                transactionRepository = transactionRepo,
                categoryRepository = categoryRepo,
                currencyRepository = currencyRepo,
                financialSessionRepository = financialSessionRepo,
                syncQueueRepository = syncQueueRepo,
            ),
    )
}
