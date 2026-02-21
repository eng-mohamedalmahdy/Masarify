package com.lightfeather.masarify.page.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCategorySpending
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiQuickStats
import com.lightfeather.designsystem.model.UiSpendingAnalytics
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetTotalExpenseOfCurrency
import com.lightfeather.domain.usecase.GetTotalIncomeOfCurrency
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.mappers.toUiTransaction
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.AccountsRoute
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import com.lightfeather.masarify.util.formatAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel for Dashboard page
 * Handles loading dashboard data, month navigation, currency selection, and navigation
 *
 * @property getAllAccounts Use case to fetch all bank accounts
 * @property getWealthWorthInCurrency Use case to calculate total wealth in different currencies
 * @property getAllTransactionsPaged Use case to fetch recent transactions
 * @property getTotalExpenseOfCurrency Use case to get total expenses for a currency
 * @property getTotalIncomeOfCurrency Use case to get total income for a currency
 * @property userRepository Repository for user preferences and data
 * @property navigator Navigator for page navigation
 */
@Suppress("LongParameterList")
internal class DashboardPageViewModel(
    private val getAllAccounts: GetAllAccounts,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val getAllTransactionsPaged: GetAllTransactionsPaged,
    private val getTotalExpenseOfCurrency: GetTotalExpenseOfCurrency,
    private val getTotalIncomeOfCurrency: GetTotalIncomeOfCurrency,
    private val userRepository: UserRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardPageState())
    val state: StateFlow<DashboardPageState> = _state.asStateFlow()

    init {
        onIntent(DashboardPageIntent.LoadData)
    }

    fun onIntent(intent: DashboardPageIntent) {
        when (intent) {
            is DashboardPageIntent.LoadData -> loadData()
            is DashboardPageIntent.NextMonth -> navigateToNextMonth()
            is DashboardPageIntent.PreviousMonth -> navigateToPreviousMonth()
            is DashboardPageIntent.SelectCurrency -> selectCurrency(intent.currency)
            is DashboardPageIntent.NavigateToAccounts -> navigateToAccounts()
            is DashboardPageIntent.NavigateToTransactions -> navigateToTransactions()
            is DashboardPageIntent.NavigateToAddAccount -> navigateToAddAccount()
            is DashboardPageIntent.NavigateToAccount -> navigateToAccount(intent.account)
            is DashboardPageIntent.NavigateToTransaction -> navigateToTransaction(intent.transaction)
        }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Load user data for greeting
            loadUserGreeting()

            // Initialize month to current month
            val now =
                kotlin.time.Clock.System
                    .now()
            val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            updateSelectedMonth(localDateTime.year, localDateTime.monthNumber)

            // Load accounts
            loadAccounts()

            // Load currencies and wealth
            loadWealthAndCurrencies()

            // Load recent transactions
            loadRecentTransactions()

            // Calculate spending analytics
            calculateSpendingAnalytics()

            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadUserGreeting() {
        val userData = userRepository.getUserData().getOrNull()
        val userName = userData?.userName ?: ""
        val greeting = calculateGreeting()

        _state.update {
            it.copy(
                userName = userName,
                greeting = greeting,
            )
        }
    }

    private fun calculateGreeting(): String {
        val hour =
            kotlin.time.Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .hour
        return when (hour) {
            in 0..11 -> "good_morning"
            in 12..16 -> "good_afternoon"
            in 17..20 -> "good_evening"
            else -> "good_night"
        }
    }

    private suspend fun loadAccounts() {
        when (val result = getAllAccounts()) {
            is DomainResult.Success -> {
                val accountsFlow =
                    result.data.map { accounts ->
                        accounts.map { it.toUiBankAccount() }
                    }
                _state.update { it.copy(accounts = accountsFlow) }

                // Calculate quick stats
                calculateQuickStats()
            }
            is DomainResult.Failure -> {
                SnackbarService.sendErrorMessage(MR.strings.account_fetch_failure)
            }
        }
    }

    private suspend fun loadWealthAndCurrencies() {
        when (val result = getWealthWorthInCurrency()) {
            is DomainResult.Success -> {
                val wealthFlow = result.data.firstOrNull() ?: emptyList()
                if (wealthFlow.isNotEmpty()) {
                    val currencies = wealthFlow.map { it.currency.toUiCurrency() }
                    val currenciesFlow = flowOf(currencies)

                    // Select first currency by default if none selected
                    val currentCurrency = _state.value.selectedCurrency ?: currencies.firstOrNull()
                    val totalWorth =
                        wealthFlow
                            .find {
                                it.currency ==
                                    currentCurrency?.let {
                                        currencies
                                            .find { uiCurr -> uiCurr.id == currentCurrency.id }
                                            ?.let {
                                                wealthFlow
                                                    .find { wealth ->
                                                        wealth.currency.id.toString() == it.id
                                                    }?.currency
                                            }
                                    }
                            }?.worth ?: 0.0

                    _state.update {
                        it.copy(
                            availableCurrencies = currenciesFlow,
                            selectedCurrency = currentCurrency,
                            totalBalance = formatAmount(totalWorth),
                        )
                    }
                }
            }
            is DomainResult.Failure -> {
                SnackbarService.sendErrorMessage(MR.strings.currency_fetch_failure)
            }
        }
    }

    private suspend fun loadRecentTransactions() {
        when (val result = getAllTransactionsPaged(page = 0)) {
            is DomainResult.Success -> {
                val transactionsFlow =
                    result.data.map { pagedData ->
                        pagedData.data.map { it.toUiTransaction() }
                    }
                _state.update { it.copy(recentTransactions = transactionsFlow) }
            }
            is DomainResult.Failure -> {
                SnackbarService.sendErrorMessage(MR.strings.transaction_fetch_failure)
            }
        }
    }

    private suspend fun calculateQuickStats() {
        // For now, use simple aggregation by account names
        // In the future, could categorize by account type field
        val accounts = _state.value.accounts.firstOrNull() ?: emptyList()

        var cashTotal = 0.0
        var debitTotal = 0.0
        var creditTotal = 0.0

        accounts.forEach { account ->
            val balance = account.balance.toDoubleOrNull() ?: 0.0
            // Simple heuristic: categorize by account name keywords
            when {
                account.name.contains("cash", ignoreCase = true) -> cashTotal += balance
                account.name.contains("credit", ignoreCase = true) -> creditTotal += balance
                else -> debitTotal += balance
            }
        }

        _state.update {
            it.copy(
                quickStats =
                    UiQuickStats(
                        cash = formatAmount(cashTotal),
                        debit = formatAmount(debitTotal),
                        credit = formatAmount(creditTotal),
                    ),
            )
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private suspend fun calculateSpendingAnalytics() {
        _state.update { it.copy(isAnalyticsLoading = true) }

        try {
            delay(ANALYTICS_DELAY_MS) // Small delay for UX

            val selectedCurrency = _state.value.selectedCurrency
            if (selectedCurrency == null) {
                _state.update { it.copy(isAnalyticsLoading = false) }
                return
            }

            // Get domain currency from UI currency
            val domainCurrency =
                when (val wealthResult = getWealthWorthInCurrency()) {
                    is DomainResult.Success -> {
                        wealthResult.data
                            .firstOrNull()
                            ?.find { it.currency.id.toString() == selectedCurrency.id }
                            ?.currency
                    }
                    else -> null
                }

            if (domainCurrency == null) {
                _state.update { it.copy(isAnalyticsLoading = false) }
                return
            }

            // Calculate total expenses and income for the month
            // Use cases return DomainResult<Flow<Double>>, so we need to collect the Flow
            val totalExpense =
                when (
                    val expenseResult = getTotalExpenseOfCurrency<Transaction.Expense>(domainCurrency)
                ) {
                    is DomainResult.Success -> expenseResult.data.firstOrNull() ?: 0.0
                    is DomainResult.Failure -> 0.0
                }

            val totalIncome =
                when (val incomeResult = getTotalIncomeOfCurrency(domainCurrency)) {
                    is DomainResult.Success -> incomeResult.data.firstOrNull() ?: 0.0
                    is DomainResult.Failure -> 0.0
                }

            val total = totalExpense + totalIncome
            val spendingPercentage = if (total > 0) (totalExpense / total).toFloat() else 0f
            val savingPercentage = if (total > 0) (totalIncome / total).toFloat() else 0f

            // For category breakdown, we'll use a simplified approach
            // In a real app, you'd query transactions by category for the selected month
            val categoryBreakdown = emptyList<UiCategorySpending>()

            _state.update {
                it.copy(
                    income = formatAmount(totalIncome),
                    expense = formatAmount(totalExpense),
                    spendingAnalytics =
                        UiSpendingAnalytics(
                            spendingPercentage = spendingPercentage,
                            savingPercentage = savingPercentage,
                            totalSpending = formatAmount(totalExpense),
                            totalSaving = formatAmount(totalIncome),
                            categoryBreakdown = categoryBreakdown,
                        ),
                    isAnalyticsLoading = false,
                )
            }
        } catch (e: Exception) {
            // Logging would be added here in production
            _state.update { it.copy(isAnalyticsLoading = false) }
        }
    }

    private fun navigateToNextMonth() {
        val currentTimestamp = _state.value.selectedMonthTimestamp
        val currentDate =
            Instant
                .fromEpochMilliseconds(currentTimestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())

        val nextMonth = if (currentDate.monthNumber == 12) 1 else currentDate.monthNumber + 1
        val nextYear = if (currentDate.monthNumber == 12) currentDate.year + 1 else currentDate.year

        updateSelectedMonth(nextYear, nextMonth)
        viewModelScope.launch {
            calculateSpendingAnalytics()
        }
    }

    private fun navigateToPreviousMonth() {
        val currentTimestamp = _state.value.selectedMonthTimestamp
        val currentDate =
            Instant
                .fromEpochMilliseconds(currentTimestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())

        val prevMonth = if (currentDate.monthNumber == 1) 12 else currentDate.monthNumber - 1
        val prevYear = if (currentDate.monthNumber == 1) currentDate.year - 1 else currentDate.year

        updateSelectedMonth(prevYear, prevMonth)
        viewModelScope.launch {
            calculateSpendingAnalytics()
        }
    }

    private fun updateSelectedMonth(
        year: Int,
        month: Int,
    ) {
        val monthName = getMonthName(month)
        val displayText = "$monthName $year"
        val timestamp =
            LocalDateTime(year, month, 1, 0, 0)
                .toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()

        _state.update {
            it.copy(
                selectedMonth = displayText,
                selectedMonthTimestamp = timestamp,
            )
        }
    }

    private fun getMonthName(month: Int): String =
        when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }

    private fun selectCurrency(currency: UiCurrency) {
        _state.update { it.copy(selectedCurrency = currency) }
        viewModelScope.launch {
            loadWealthAndCurrencies()
            calculateQuickStats()
            calculateSpendingAnalytics()
        }
    }

    private fun navigateToAccounts() {
        navigator.navigate(AccountsRoute)
    }

    private fun navigateToTransactions() {
        navigator.navigate(TransactionsRoute())
    }

    private fun navigateToAddAccount() {
        // Navigate to accounts page which has FAB for adding account
        navigator.navigate(AccountsRoute)
    }

    @Suppress("UnusedParameter")
    private fun navigateToAccount(account: UiBankAccount) {
        // Navigate to accounts page
        // The account selection logic is handled by BankAccountsPage
        navigator.navigate(AccountsRoute)
    }

    @Suppress("UnusedParameter")
    private fun navigateToTransaction(transaction: UiTransaction) {
        // Navigate to transactions page
        navigator.navigate(TransactionsRoute())
    }

    companion object {
        private const val ANALYTICS_DELAY_MS = 300L
    }
}
