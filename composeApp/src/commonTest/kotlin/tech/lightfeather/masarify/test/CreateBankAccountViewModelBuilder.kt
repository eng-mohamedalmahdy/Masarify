package tech.lightfeather.masarify.test

import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.domain.usecase.BankNameUseCase
import tech.lightfeather.domain.usecase.CreateAccount
import tech.lightfeather.domain.usecase.CreateCurrency
import tech.lightfeather.domain.usecase.GetAllCurrencies
import tech.lightfeather.domain.usecase.GetUserSavedColors
import tech.lightfeather.domain.usecase.SaveUserColor
import tech.lightfeather.domain.usecase.SetDefaultAccount
import tech.lightfeather.domain.usecase.UpdateAccount
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.createbankaccount.CreateBankAccountPageViewModel

internal fun buildCreateBankAccountViewModel(
    account: UiBankAccount = UiBankAccount.empty,
    navigator: Navigator = CapturingNavigator(),
): CreateBankAccountPageViewModel {
    val accountRepo = FakeAccountRepository()
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val bankNameRepo = FakeBankNameRepository()
    val userRepo = FakeUserRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return CreateBankAccountPageViewModel(
        account = account,
        navigator = navigator,
        createAccount = CreateAccount(accountRepo, syncHelper, currencyRepo),
        updateAccount = UpdateAccount(accountRepo, syncHelper, currencyRepo),
        getAllCurrencies = GetAllCurrencies(currencyRepo),
        getUserSavedColors = GetUserSavedColors(userRepo),
        saveColor = SaveUserColor(userRepo),
        createCurrency = CreateCurrency(currencyRepo, exchangeRateRepo, syncHelper),
        getAllBankNames = BankNameUseCase.GetAllBankNames(bankNameRepo),
        createBankName = BankNameUseCase.CreateBankName(bankNameRepo),
        setDefaultAccount = SetDefaultAccount(accountRepo),
    )
}
