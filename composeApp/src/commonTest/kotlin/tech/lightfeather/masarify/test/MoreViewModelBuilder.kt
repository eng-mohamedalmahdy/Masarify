package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.ClearLocalDataUseCase
import tech.lightfeather.domain.usecase.DeleteAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.DeleteFailedSyncEntryUseCase
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.ExportDataUseCase
import tech.lightfeather.domain.usecase.GetFailedSyncCountUseCase
import tech.lightfeather.domain.usecase.GetFailedSyncEntriesUseCase
import tech.lightfeather.domain.usecase.GetUserDarkMode
import tech.lightfeather.domain.usecase.GetUserLanguage
import tech.lightfeather.domain.usecase.ImportDataUseCase
import tech.lightfeather.domain.usecase.IsAuthenticatedUseCase
import tech.lightfeather.domain.usecase.IsEmailVerifiedUseCase
import tech.lightfeather.domain.usecase.IsProActiveUseCase
import tech.lightfeather.domain.usecase.LogoutAllDevicesUseCase
import tech.lightfeather.domain.usecase.LogoutUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.ResendVerificationUseCase
import tech.lightfeather.domain.usecase.RetryAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.RetrySyncEntryUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.more.MorePageViewModel

internal fun buildMoreViewModel(navigator: Navigator = CapturingNavigator()): MorePageViewModel {
    val authRepo = FakeAuthRepository()
    val userRepo = FakeUserRepository()
    val syncQueueRepo = FakeSyncQueueRepository()
    val syncRepo = FakeSyncRepository()
    val accountRepo = FakeAccountRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val currencyRepo = FakeCurrencyRepository()
    val financialSessionRepo = FakeFinancialSessionRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val backupRepo = FakeBackupRepository()
    val drainOutbox = DrainOutboxQueueUseCase(syncQueueRepo, syncRepo)
    val clearLocalData = ClearLocalDataUseCase(backupRepo, userRepo)
    return MorePageViewModel(
        isDarkModeEnabled = GetUserDarkMode(userRepo),
        getLanguage = GetUserLanguage(userRepo),
        userRepository = userRepo,
        exportDataUseCase = ExportDataUseCase(backupRepo),
        importDataUseCase = ImportDataUseCase(backupRepo),
        logoutUseCase = LogoutUseCase(authRepo, clearLocalData),
        logoutAllDevicesUseCase = LogoutAllDevicesUseCase(authRepo, clearLocalData),
        isEmailVerifiedUseCase = IsEmailVerifiedUseCase(authRepo),
        resendVerificationUseCase = ResendVerificationUseCase(authRepo),
        navigator = navigator,
        isAuthenticatedUseCase = IsAuthenticatedUseCase(authRepo),
        uploadLocalDataUseCase =
            UploadLocalDataUseCase(
                accountRepository = accountRepo,
                transactionRepository = transactionRepo,
                categoryRepository = categoryRepo,
                currencyRepository = currencyRepo,
                financialSessionRepository = financialSessionRepo,
                syncQueueRepository = syncQueueRepo,
            ),
        drainOutboxQueueUseCase = drainOutbox,
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
        getFailedSyncCountUseCase = GetFailedSyncCountUseCase(syncQueueRepo),
        getFailedSyncEntriesUseCase = GetFailedSyncEntriesUseCase(syncQueueRepo),
        retrySyncEntryUseCase = RetrySyncEntryUseCase(syncQueueRepo),
        retryAllFailedSyncUseCase = RetryAllFailedSyncUseCase(syncQueueRepo, drainOutbox),
        deleteFailedSyncEntryUseCase = DeleteFailedSyncEntryUseCase(syncQueueRepo),
        deleteAllFailedSyncUseCase = DeleteAllFailedSyncUseCase(syncQueueRepo),
        isProActiveUseCase = IsProActiveUseCase(FakeSubscriptionRepository()),
    )
}
