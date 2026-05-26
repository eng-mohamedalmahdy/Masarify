package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.AcquireAndUpdateFcmUseCase
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.LoginUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.RegisterDeviceTokenUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.auth.login.LoginPageViewModel

internal fun buildLoginViewModel(
    authRepository: FakeAuthRepository = FakeAuthRepository(),
    navigator: Navigator = CapturingNavigator(),
): LoginPageViewModel {
    val syncRepo = FakeSyncRepository()
    val syncQueueRepo = FakeSyncQueueRepository()
    val accountRepo = FakeAccountRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val currencyRepo = FakeCurrencyRepository()
    val financialSessionRepo = FakeFinancialSessionRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val userRepo = FakeUserRepository()

    return LoginPageViewModel(
        loginUseCase = LoginUseCase(authRepository),
        navigator = navigator,
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
        drainOutboxQueueUseCase =
            DrainOutboxQueueUseCase(
                syncQueueRepository = syncQueueRepo,
                syncRepository = syncRepo,
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
        acquireAndUpdateFcmUseCase =
            AcquireAndUpdateFcmUseCase(
                platform = FakePlatform,
                fcmHelper = FakeFCMHelper,
                userRepository = userRepo,
                registerDeviceTokenUseCase = RegisterDeviceTokenUseCase(FakeDeviceTokenRepository()),
            ),
    )
}
