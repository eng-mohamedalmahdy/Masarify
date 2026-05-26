package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.CreateFinancialSession
import tech.lightfeather.domain.usecase.DeleteFinancialSession
import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.domain.usecase.GetAllFinancialSessions
import tech.lightfeather.domain.usecase.GetAllTransactions
import tech.lightfeather.domain.usecase.GetFilteredTransactions
import tech.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.UpdateFinancialSession
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.dashboard.DashboardPageViewModel

internal fun buildDashboardViewModel(navigator: Navigator = CapturingNavigator()): DashboardPageViewModel {
    val accountRepo = FakeAccountRepository()
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val categoryRepo = FakeCategoryRepository()
    val transactionRepo = FakeTransactionRepository()
    val financialSessionRepo = FakeFinancialSessionRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val userRepo = FakeUserRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return DashboardPageViewModel(
        getAllAccounts = GetAllAccounts(accountRepo),
        getWealthWorthInCurrency = GetWealthWorthInCurrency(accountRepo, exchangeRateRepo, currencyRepo),
        getFilteredTransactionsPaged = GetFilteredTransactionsPaged(transactionRepo),
        getFilteredTransactions = GetFilteredTransactions(transactionRepo),
        getAllCategories = GetAllCategories(categoryRepo),
        getAllTransactions = GetAllTransactions(transactionRepo),
        deleteTransaction = DeleteTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        updateTransaction = UpdateTransaction(transactionRepo, syncHelper, accountRepo, categoryRepo),
        attachmentRepository = attachmentRepo,
        userRepository = userRepo,
        navigator = navigator,
        createFinancialSession = CreateFinancialSession(financialSessionRepo, accountRepo, syncHelper),
        updateFinancialSession = UpdateFinancialSession(financialSessionRepo, syncHelper),
        deleteFinancialSession = DeleteFinancialSession(financialSessionRepo, syncHelper),
        getAllFinancialSessions = GetAllFinancialSessions(financialSessionRepo),
    )
}
