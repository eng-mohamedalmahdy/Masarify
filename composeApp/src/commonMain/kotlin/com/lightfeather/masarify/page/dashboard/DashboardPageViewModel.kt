package com.lightfeather.masarify.page.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCategorySpending
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiQuickStats
import com.lightfeather.designsystem.model.UiSpendingAnalytics
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionDetails
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.AppLanguages
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.model.transaction.transactionFilter
import com.lightfeather.domain.repository.AttachmentRepository
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.DeleteTransaction
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCategories
import com.lightfeather.domain.usecase.GetFilteredTransactions
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.domain.usecase.UpdateTransaction
import com.lightfeather.masarify.framework.FileKitHelper
import com.lightfeather.masarify.mappers.toDomainTransaction
import com.lightfeather.masarify.mappers.toUiAttachment
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCategory
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.mappers.toUiTransaction
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.AccountsRoute
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import com.lightfeather.masarify.util.formatAmount
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
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
 * @property getFilteredTransactionsPaged Use case to fetch filtered transactions with pagination
 * @property getFilteredTransactions Use case to fetch filtered transactions
 * @property getTotalExpenseOfCurrency Use case to get total expenses for a currency
 * @property getTotalIncomeOfCurrency Use case to get total income for a currency
 * @property getTotalExpensesByCategories Use case to get expenses grouped by category
 * @property getExchangeRatesOfCurrency Use case to get exchange rates for currency conversion
 * @property getAllCategories Use case to fetch all categories
 * @property deleteTransaction Use case to delete a transaction
 * @property updateTransaction Use case to update or create a transaction
 * @property attachmentRepository Repository for transaction attachments
 * @property userRepository Repository for user preferences and data
 * @property navigator Navigator for page navigation
 */
@Suppress("LongParameterList", "UnusedPrivateProperty")
internal class DashboardPageViewModel(
    private val getAllAccounts: GetAllAccounts,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    private val getFilteredTransactions: GetFilteredTransactions,
    private val getAllCategories: GetAllCategories,
    private val deleteTransaction: DeleteTransaction,
    private val updateTransaction: UpdateTransaction,
    private val attachmentRepository: AttachmentRepository,
    private val userRepository: UserRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardPageState())
    val state: StateFlow<DashboardPageState> = _state.asStateFlow()

    init {
        onIntent(DashboardPageIntent.LoadData)
    }

    @Suppress("CyclomaticComplexMethod") // Complexity due to comprehensive intent handling
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
            is DashboardPageIntent.SelectAccount -> selectAccount(intent.account)
            is DashboardPageIntent.CreateTransactionInAccount -> createTransactionInAccount(intent.account)
            is DashboardPageIntent.UpdateTransaction -> updateTransactionDialog(intent.transaction)
            is DashboardPageIntent.DeleteTransaction -> deleteTransactionDialog(intent.transaction)
            is DashboardPageIntent.DuplicateTransaction -> duplicateTransaction(intent.transaction)
            is DashboardPageIntent.ConfirmUpdateTransaction -> confirmUpdateTransaction(intent.transaction)
            is DashboardPageIntent.CancelUpdateTransaction -> cancelUpdateTransaction()
            is DashboardPageIntent.PickImages -> pickImages()
            is DashboardPageIntent.DeleteAttachment -> deleteAttachment(intent.attachment)
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

            // Load categories
            loadCategories()

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
        // Create date range filter for selected month
        val dateRange = getMonthDateRange(_state.value.selectedMonthTimestamp)
        val filter =
            transactionFilter {
                dateRange(dateRange.from!!, dateRange.to!!)
            }

        when (val result = getFilteredTransactionsPaged(filter, page = 0)) {
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

    @Suppress("TooGenericExceptionCaught", "SwallowedException", "CyclomaticComplexMethod")
    private suspend fun calculateSpendingAnalytics() {
        _state.update { it.copy(isAnalyticsLoading = true) }

        try {

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

            // Create date range filter for selected month
            val dateRange = getMonthDateRange(_state.value.selectedMonthTimestamp)
            val filter =
                transactionFilter {
//                    currencyIn(domainCurrency)
                    dateRange(dateRange.from!!, dateRange.to!!)
                }

            // Get filtered transactions for the month
            val transactions =
                when (val result = getFilteredTransactions(filter)) {
                    is DomainResult.Success -> result.data
                    is DomainResult.Failure -> emptyList()
                }

            // Calculate totals from filtered transactions
            val totalExpense =
                transactions
                    .filterIsInstance<Transaction.Expense>()
                    .sumOf { it.amount }

            val totalIncome =
                transactions
                    .filterIsInstance<Transaction.Income>()
                    .sumOf { it.amount }

            val total = totalExpense + totalIncome
            val spendingPercentage = if (total > 0) (totalExpense / total).toFloat() else 0f
            val savingPercentage = if (total > 0) (totalIncome / total).toFloat() else 0f

            // Get category breakdown for expenses
            val categoryBreakdown = buildCategoryBreakdown(totalExpense, dateRange)

            _state.update {
                it.copy(
                    income = formatAmount(totalIncome),
                    expense = formatAmount(totalExpense),
                    spendingAnalytics =
                        UiSpendingAnalytics(
                            selectedCurrency = selectedCurrency,
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

    /**
     * Build category breakdown with currency and date filtering
     * Gets expenses grouped by category for the selected month and currency
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException", "ReturnCount")
    private suspend fun buildCategoryBreakdown(
        totalExpenseInTargetCurrency: Double,
        dateRange: TransactionFilter.DateRange,
    ): List<UiCategorySpending> {
        try {
            // Create filter for expenses in target currency and date range
            val filter =
                transactionFilter {
//                    currencyIn(targetCurrency)
                    dateRange(dateRange.from!!, dateRange.to!!)
                    expenseOnly()
                }

            // Get filtered transactions
            val expenses =
                when (val result = getFilteredTransactions(filter)) {
                    is DomainResult.Success -> result.data.filterIsInstance<Transaction.Expense>()
                    is DomainResult.Failure -> return emptyList()
                }

            if (expenses.isEmpty()) {
                return emptyList()
            }

            // Group by categories and calculate totals
            val expensesByCategory = mutableMapOf<Category, Double>()
            expenses.forEach { expense ->
                expense.categories.forEach { category ->
                    expensesByCategory[category] = (expensesByCategory[category] ?: 0.0) + expense.amount
                }
            }

            // Convert to UI category spending with percentages
            val categorySpendingList =
                expensesByCategory.mapNotNull { (category, amount) ->
                    if (amount > 0) {
                        val percentage =
                            if (totalExpenseInTargetCurrency > 0) {
                                (amount / totalExpenseInTargetCurrency).toFloat()
                            } else {
                                0f
                            }

                        UiCategorySpending(
                            category = category.toUiCategory(),
                            amount = formatAmount(amount),
                            percentage = percentage,
                        )
                    } else {
                        null
                    }
                }

            // Sort by amount descending
            return categorySpendingList.sortedByDescending {
                it.amount.replace(",", "").toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            // Logging would be added here in production
            return emptyList()
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

    private fun getMonthName(month: Int): String {

        require(month in 1..12) { "Month must be between 1 and 12" }

        val months = when (userRepository.getAppLanguage()) {
            AppLanguages.English -> listOf(
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
            )

            AppLanguages.Arabic -> listOf(
                "يناير", "فبراير", "مارس", "أبريل",
                "مايو", "يونيو", "يوليو", "أغسطس",
                "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
            )

            else -> listOf()
        }

        return months[month - 1]
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

    private fun navigateToAccount(account: UiBankAccount) {
        // Update state to show account in detail pane (handled by adaptive UI)
        _state.update { it.copy(selectedAccount = account) }
    }

    private fun navigateToTransaction(transaction: UiTransaction) {
        // Update state to show transaction in extra pane (handled by adaptive UI)
        _state.update { it.copy(selectedTransaction = transaction) }
    }

    private fun selectAccount(account: UiBankAccount?) {
        _state.update { it.copy(selectedAccount = account) }
    }

    // Transaction management methods

    private fun createTransactionInAccount(account: UiBankAccount) {
        viewModelScope.launch {
            // Navigate to transactions page with add dialog open and account locked
            navigator.navigate(
                TransactionsRoute(
                    openAddDialog = true,
                    fromAccountId = account.id,
                ),
            )
        }
    }

    private fun updateTransactionDialog(transaction: UiTransactionDetails) {
        loadAttachments(transaction.id)
        viewModelScope.launch {
            delay(100) // Wait for attachments to load
            val attachments = _state.value.transactionAttachments[transaction.id] ?: emptyList()
            _state.update {
                it.copy(
                    showAddEditDialog = true,
                    underProcessTransaction = transaction,
                    selectedAttachments = attachments,
                )
            }
        }
    }

    private fun deleteTransactionDialog(transaction: UiTransactionDetails) {
        loadAttachments(transaction.id)
        viewModelScope.launch {
            delay(100) // Wait for attachments to load
            _state.update {
                it.copy(
                    showAddEditDialog = true,
                    underProcessTransaction = transaction,
                )
            }

            // Delete the transaction
            deleteTransaction(transaction.id.toLong()).foldSuspend(
                onSuccess = {
                    _state.update { it.copy(showAddEditDialog = false, underProcessTransaction = null) }
                    SnackbarService.sendSuccessMessage(MR.strings.transaction_delete_success)
                    // Reload data
                    loadRecentTransactions()
                    calculateSpendingAnalytics()
                },
                onFailure = {
                    _state.update { it.copy(showAddEditDialog = false) }
                    SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                },
            )
        }
    }

    @Suppress("UnusedParameter", "UnusedPrivateProperty")
    private fun duplicateTransaction(transaction: UiTransactionDetails) {
        // Transaction duplication will be implemented in a future release
    }

    private fun confirmUpdateTransaction(transaction: UiTransactionData) {
        viewModelScope.launch {
            updateTransaction(transaction.toDomainTransaction()).fold(
                onSuccess = {
                    _state.update { it.copy(showAddEditDialog = false, underProcessTransaction = null) }
                    SnackbarService.sendSuccessMessage(MR.strings.transaction_update_success)
                    viewModelScope.launch {
                        // Reload data
                        launch {
                            loadRecentTransactions()
                        }
                        launch {
                            calculateSpendingAnalytics()
                        }
                    }
                },
                onFailure = {
                    _state.update { it.copy(showAddEditDialog = false) }
                    SnackbarService.sendErrorMessage(MR.strings.transaction_update_failure)
                },
            )
        }
    }

    private fun cancelUpdateTransaction() {
        _state.update {
            it.copy(
                showAddEditDialog = false,
                underProcessTransaction = null,
                selectedAttachments = emptyList(),
            )
        }
    }

    @Suppress("TooGenericExceptionCaught") // General error handling for FileKit operations
    private fun pickImages() {
        viewModelScope.launch {
            try {
                val files =
                    FileKit.openFilePicker(
                        type = FileKitType.Image,
                        mode = FileKitMode.Multiple(),
                    )
                if (files != null) {
                    val newAttachments = mutableListOf<UiAttachment>()
                    files.forEach { file ->
                        val bytes = file.readBytes()
                        val compressedBytes = FileKitHelper.compressImage(bytes)
                        if (FileKitHelper.isValidImage(compressedBytes, file.mimeType())) {
                            newAttachments.add(
                                UiAttachment(
                                    id = "-1",
                                    name = file.name,
                                    mimeType = "image/jpeg",
                                    fileContent = compressedBytes,
                                ),
                            )
                        }
                    }
                    _state.update { it.copy(selectedAttachments = it.selectedAttachments + newAttachments) }
                }
            } catch (e: Exception) {
                Napier.e("Error picking images", e)
                SnackbarService.sendErrorMessage(MR.strings.unknown_error)
            }
        }
    }

    private fun deleteAttachment(attachment: UiAttachment) {
        _state.update { it.copy(selectedAttachments = it.selectedAttachments - attachment) }
    }

    private fun loadAttachments(transactionId: String) {
        viewModelScope.launch {
            when (val result = attachmentRepository.getAttachmentsByTransactionId(transactionId.toIntOrNull() ?: -1)) {
                is DomainResult.Success -> {
                    val attachments = result.data.map { it.toUiAttachment() }
                    _state.update {
                        it.copy(
                            transactionAttachments =
                                it.transactionAttachments + (transactionId to attachments),
                        )
                    }
                }

                is DomainResult.Failure -> {
                    _state.update {
                        it.copy(
                            transactionAttachments =
                                it.transactionAttachments + (transactionId to emptyList()),
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadCategories() {
        when (val result = getAllCategories()) {
            is DomainResult.Success -> {
                val categories = result.data.firstOrNull()?.map { it.toUiCategory() } ?: emptyList()
                _state.update { it.copy(categories = categories) }
            }

            is DomainResult.Failure -> {
                SnackbarService.sendErrorMessage(MR.strings.category_fetch_failure)
            }
        }
    }

    /**
     * Calculate date range for a given month timestamp
     * Returns a DateRange with start and end of month
     */
    private fun getMonthDateRange(timestamp: Long): TransactionFilter.DateRange {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Start of month: first day at 00:00:00
        val startOfMonth =
            LocalDateTime(
                year = dateTime.year,
                monthNumber = dateTime.monthNumber,
                dayOfMonth = 1,
                hour = 0,
                minute = 0,
                second = 0,
            )

        // End of month: last day at 23:59:59
        val daysInMonth = getDaysInMonth(dateTime.year, dateTime.monthNumber)
        val endOfMonth =
            LocalDateTime(
                year = dateTime.year,
                monthNumber = dateTime.monthNumber,
                dayOfMonth = daysInMonth,
                hour = 23,
                minute = 59,
                second = 59,
            )

        return TransactionFilter.DateRange(from = startOfMonth, to = endOfMonth)
    }

    /**
     * Get number of days in a given month
     */
    private fun getDaysInMonth(
        year: Int,
        month: Int,
    ): Int =
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }

    /**
     * Check if a year is a leap year
     */
    private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    companion object {
        private const val ANALYTICS_DELAY_MS = 300L
    }
}
