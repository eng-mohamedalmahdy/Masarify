package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.BankNameUseCase
import tech.lightfeather.domain.usecase.CreateAccount
import tech.lightfeather.domain.usecase.CreateCurrency
import tech.lightfeather.domain.usecase.GetAllCurrencies
import tech.lightfeather.domain.usecase.MarkOnboardingComplete
import tech.lightfeather.domain.usecase.SetDefaultAccount
import tech.lightfeather.domain.usecase.UpsertUserData
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.onboarding.OnBoardingPageViewModel

internal fun buildOnBoardingViewModel(navigator: Navigator = CapturingNavigator()): OnBoardingPageViewModel {
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val accountRepo = FakeAccountRepository()
    val userRepo = FakeUserRepository()
    val bankNameRepo = FakeBankNameRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return OnBoardingPageViewModel(
        createCurrency = CreateCurrency(currencyRepo, exchangeRateRepo, syncHelper),
        createAccount = CreateAccount(accountRepo, syncHelper, currencyRepo),
        upsertUserData = UpsertUserData(userRepo),
        navigator = navigator,
        getAllCurrencies = GetAllCurrencies(currencyRepo),
        getAllBankNames = BankNameUseCase.GetAllBankNames(bankNameRepo),
        createBankName = BankNameUseCase.CreateBankName(bankNameRepo),
        setDefaultAccount = SetDefaultAccount(accountRepo),
        markOnboardingComplete = MarkOnboardingComplete(userRepo),
    )
}
