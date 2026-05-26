package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.CreateTransaction
import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import tech.lightfeather.domain.usecase.GetDefaultAccount
import tech.lightfeather.domain.usecase.GetUsedCurrencies
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.masarify.page.transactions.TransactionsPageViewModel

internal fun buildTransactionsViewModel(): TransactionsPageViewModel {
    val accountRepo = FakeAccountRepository()
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val transactionRepo = FakeTransactionRepository()
    val categoryRepo = FakeCategoryRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return TransactionsPageViewModel(
        getAccountsUseCase = GetAllAccounts(accountRepo),
        categoriesUseCase = GetAllCategories(categoryRepo),
        getCurrenciesUseCase = GetUsedCurrencies(currencyRepo),
        createTransactionUseCase = CreateTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        updateTransactionUseCase = UpdateTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        deleteTransactionUseCase = DeleteTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        attachmentRepository = attachmentRepo,
        getWealthWorthInCurrency = GetWealthWorthInCurrency(accountRepo, exchangeRateRepo, currencyRepo),
        exchangeRates = GetAllCurrenciesExchangeRates(exchangeRateRepo),
        getDefaultAccount = GetDefaultAccount(accountRepo),
    )
}
