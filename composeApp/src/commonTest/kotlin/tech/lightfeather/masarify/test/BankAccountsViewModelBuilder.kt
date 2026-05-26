package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import tech.lightfeather.domain.usecase.GetUsedCurrencies
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.bankaccounts.BankAccountsPageViewModel

internal fun buildBankAccountsViewModel(navigator: Navigator = CapturingNavigator()): BankAccountsPageViewModel {
    val accountRepo = FakeAccountRepository()
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return BankAccountsPageViewModel(
        navigator = navigator,
        getAllAccounts = GetAllAccounts(accountRepo),
        getAllCurrencies = GetUsedCurrencies(currencyRepo),
        getWealthWorthInCurrency = GetWealthWorthInCurrency(accountRepo, exchangeRateRepo, currencyRepo),
        exchangeRates = GetAllCurrenciesExchangeRates(exchangeRateRepo),
        deleteTransaction = DeleteTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        updateTransaction = UpdateTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        attachmentRepository = attachmentRepo,
    )
}
