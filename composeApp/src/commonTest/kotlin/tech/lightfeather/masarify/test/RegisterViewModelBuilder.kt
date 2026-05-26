package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.RegisterUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.auth.register.RegisterPageViewModel

internal fun buildRegisterViewModel(
    authRepository: FakeAuthRepository = FakeAuthRepository(),
    navigator: Navigator = CapturingNavigator(),
): RegisterPageViewModel {
    val syncQueueRepo = FakeSyncQueueRepository()
    val syncRepo = FakeSyncRepository()
    val accountRepo = FakeAccountRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val currencyRepo = FakeCurrencyRepository()
    val financialSessionRepo = FakeFinancialSessionRepository()

    return RegisterPageViewModel(
        registerUseCase = RegisterUseCase(authRepository),
        navigator = navigator,
        uploadLocalDataUseCase =
            UploadLocalDataUseCase(
                accountRepository = accountRepo,
                transactionRepository = transactionRepo,
                categoryRepository = categoryRepo,
                currencyRepository = currencyRepo,
                financialSessionRepository = financialSessionRepo,
                syncQueueRepository = syncQueueRepo,
            ),
        drainOutboxQueueUseCase =
            DrainOutboxQueueUseCase(
                syncQueueRepository = syncQueueRepo,
                syncRepository = syncRepo,
            ),
    )
}
