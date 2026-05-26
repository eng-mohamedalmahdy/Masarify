package tech.lightfeather.masarify.page.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import tech.lightfeather.designsystem.model.PageSize
import tech.lightfeather.designsystem.model.SavedFilter
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionDetails
import tech.lightfeather.designsystem.model.UiTransactionFilter
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.usecase.CreateTransaction
import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import tech.lightfeather.domain.usecase.GetDefaultAccount
import tech.lightfeather.domain.usecase.GetUsedCurrencies
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.masarify.framework.FileKitHelper
import tech.lightfeather.masarify.mappers.toAccount
import tech.lightfeather.masarify.mappers.toCategory
import tech.lightfeather.masarify.mappers.toDomainTransaction
import tech.lightfeather.masarify.mappers.toUiAttachment
import tech.lightfeather.masarify.mappers.toUiBankAccount
import tech.lightfeather.masarify.mappers.toUiCategory
import tech.lightfeather.masarify.mappers.toUiCurrency
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * ViewModel for the Transactions Page
 * Manages transaction CRUD, filtering, pagination, and UI state
 * Multiple use case dependencies are acceptable for ViewModels
 */
@Suppress("LongParameterList")
class TransactionsPageViewModel(
    private val getAccountsUseCase: GetAllAccounts,
    private val categoriesUseCase: GetAllCategories,
    private val getCurrenciesUseCase: GetUsedCurrencies,
    private val createTransactionUseCase: CreateTransaction,
    private val updateTransactionUseCase: UpdateTransaction,
    private val deleteTransactionUseCase: DeleteTransaction,
    private val attachmentRepository: AttachmentRepository,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val exchangeRates: GetAllCurrenciesExchangeRates,
    private val getDefaultAccount: GetDefaultAccount,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            TransactionsPageState(
                accounts =
                    getAccountsUseCase().foldResult(
                        onSuccess = { accountsFlow ->
                            accountsFlow.map { accounts ->
                                accounts.map { account -> account.toUiBankAccount() }
                            }
                        },
                        onFailure = { error -> flowOf() },
                    ),
            ),
        )
    val state: StateFlow<TransactionsPageState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            val currenciesFlow =
                getCurrenciesUseCase().foldResult(
                    onSuccess = { currencies ->
                        currencies.map { currencies ->
                            currencies.map { currency -> currency.toUiCurrency() }
                        }
                    },
                    onFailure = { error -> flowOf() },
                )
            _state.update { it.copy(userAccountsCurrencies = currenciesFlow) }
            launch {
                currenciesFlow.collect { currencies ->
                    _state.update { it.copy(selectedCurrency = currencies.firstOrNull()) }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getDefaultAccount().foldSuspend(
                onSuccess = { defaultAccountFlow ->
                    defaultAccountFlow.collect { account ->
                        _state.update { it.copy(defaultAccount = account?.toUiBankAccount()) }
                    }
                },
                onFailure = { },
            )
        }
    }

    /**
     * Handle user intents
     * Intent handler with multiple branches - complexity is acceptable for centralized intent routing
     */
    @Suppress("CyclomaticComplexMethod")
    fun onIntent(intent: TransactionsPageIntent) {
        when (intent) {
            is TransactionsPageIntent.LoadData -> loadData()
            is TransactionsPageIntent.SelectTransaction -> selectTransaction(intent.transaction)
            is TransactionsPageIntent.UpdateFilter -> updateFilter(intent.filter)
            is TransactionsPageIntent.SaveFilter -> saveFilter(intent.name, intent.filter)
            is TransactionsPageIntent.LoadSavedFilter -> loadSavedFilter(intent.filter)
            is TransactionsPageIntent.DeleteSavedFilter -> deleteSavedFilter(intent.filter)
            is TransactionsPageIntent.ClearFilter -> clearFilter()
            is TransactionsPageIntent.ShowFilterDialog -> showFilterDialog()
            is TransactionsPageIntent.HideFilterDialog -> hideFilterDialog()
            is TransactionsPageIntent.ChangePage -> changePage(intent.page)
            is TransactionsPageIntent.ChangePageSize -> changePageSize(intent.size)
            is TransactionsPageIntent.NextPage -> nextPage()
            is TransactionsPageIntent.PreviousPage -> previousPage()
            is TransactionsPageIntent.ShowAddDialogWithType ->
                prepareAddTransactionContext(intent.type, intent.fromAccountId, intent.categoryId)
            is TransactionsPageIntent.PrepareEditTransaction -> prepareEditTransaction(intent.transaction)
            is TransactionsPageIntent.ClearTransactionContext -> clearTransactionContext()
            is TransactionsPageIntent.CreateTransaction -> createTransaction(intent.data)
            is TransactionsPageIntent.UpdateTransaction -> updateTransaction(intent.data)
            is TransactionsPageIntent.DeleteTransaction -> deleteTransaction(intent.transaction)
            is TransactionsPageIntent.DuplicateTransaction -> duplicateTransaction(intent.transaction)
            is TransactionsPageIntent.PickImages -> pickImages()
            is TransactionsPageIntent.DeleteAttachment -> deleteAttachment(intent.attachment)
            is TransactionsPageIntent.LoadAttachments -> loadAttachments(intent.transactionId)
            is TransactionsPageIntent.SelectCurrency -> selectCurrency(intent.currency)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load reference data: categories, currencies
            categoriesUseCase().fold(
                onSuccess = { categoriesFlow ->
                    _state.update {
                        it.copy(categories = categoriesFlow.map { list -> list.map { cat -> cat.toUiCategory() } })
                    }
                },
                onFailure = { error ->
                    SnackbarService.sendErrorMessage(MR.strings.category_fetch_failure)
                },
            )

            getCurrenciesUseCase().fold(
                onSuccess = { currenciesFlow ->
                    _state.update {
                        it.copy(currencies = currenciesFlow.map { list -> list.map { cur -> cur.toUiCurrency() } })
                    }
                },
                onFailure = { error ->
                    SnackbarService.sendErrorMessage(MR.strings.currency_fetch_failure)
                },
            )

            // Load wealth worth listening (this will handle accounts loading with currency conversion)
            loadWealthWorthListening()
        }
    }

    private fun selectTransaction(transaction: UiTransaction?) {
        _state.update { it.copy(selectedTransaction = transaction) }
        // Load attachments for the selected transaction
        transaction?.let {
            loadAttachments(it.id)
        }
    }

    private fun updateFilter(filter: UiTransactionFilter) {
        _state.update { it.copy(filter = filter, currentPage = 0) }
    }

    @OptIn(ExperimentalTime::class)
    private fun saveFilter(
        name: String,
        filter: UiTransactionFilter,
    ) {
        viewModelScope.launch {
            val newFilter =
                SavedFilter(
                    id =
                        Clock.System
                            .now()
                            .toEpochMilliseconds()
                            .toString(),
                    name = name,
                    filter = filter,
                    createdAt =
                        Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()),
                )

            _state.update {
                it.copy(
                    savedFilters = it.savedFilters + newFilter,
                )
            }

            SnackbarService.sendSuccessMessage(MR.strings.filter_saved_success)
        }
    }

    private fun loadSavedFilter(filter: SavedFilter) {
        updateFilter(filter.filter)
        hideFilterDialog()
    }

    private fun deleteSavedFilter(filter: SavedFilter) {
        _state.update {
            it.copy(
                savedFilters = it.savedFilters - filter,
            )
        }
        SnackbarService.sendSuccessMessage(MR.strings.saved_filter_deleted)
    }

    private fun clearFilter() {
        updateFilter(UiTransactionFilter.EMPTY)
    }

    private fun showFilterDialog() {
        _state.update { it.copy(showFilterDialog = true) }
    }

    private fun hideFilterDialog() {
        _state.update { it.copy(showFilterDialog = false) }
    }

    private fun changePage(page: Int) {
        _state.update { it.copy(currentPage = page) }
    }

    private fun changePageSize(size: PageSize) {
        _state.update {
            it.copy(
                pageSize = size,
                currentPage = 0,
            )
        }
    }

    private fun nextPage() {
        if (_state.value.hasNextPage) {
            changePage(_state.value.currentPage + 1)
        }
    }

    private fun previousPage() {
        if (_state.value.hasPreviousPage) {
            changePage(_state.value.currentPage - 1)
        }
    }

    @Suppress("UnusedParameter") // type parameter reserved for future type-specific defaults
    private fun prepareAddTransactionContext(
        type: UiTransactionType,
        fromAccountId: String?,
        categoryId: String? = null,
    ) {
        _state.update {
            it.copy(
                editingTransaction = null,
                lockedFromAccount = null,
                initialCategory = null,
            )
        }

        if (fromAccountId != null) {
            viewModelScope.launch {
                _state.value.accounts
                    .map { accounts -> accounts.find { it.id == fromAccountId } }
                    .collect { account ->
                        _state.update { it.copy(lockedFromAccount = account) }
                    }
            }
        }

        if (categoryId != null) {
            viewModelScope.launch {
                _state.value.categories
                    .map { cats -> cats.find { it.id == categoryId } }
                    .collect { category ->
                        _state.update { it.copy(initialCategory = category) }
                    }
            }
        }
    }

    private fun prepareEditTransaction(transaction: UiTransactionDetails) {
        loadAttachments(transaction.id)

        viewModelScope.launch {
            kotlinx.coroutines.delay(100)
            val attachments = _state.value.transactionAttachments[transaction.id] ?: emptyList()
            _state.update {
                it.copy(
                    editingTransaction = transaction,
                    lockedFromAccount = null,
                    selectedAttachments = attachments,
                )
            }
        }
    }

    private fun clearTransactionContext() {
        _state.update {
            it.copy(
                editingTransaction = null,
                lockedFromAccount = null,
                initialCategory = null,
                selectedAttachments = emptyList(),
            )
        }
    }

    private fun createTransaction(data: UiTransactionData) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Convert UI transaction data to domain transaction
                Napier.d { "TOBE_ADDED $data" }
                val domainTransaction = data.toDomainTransaction()

                // Call create transaction use case
                createTransactionUseCase(domainTransaction).fold(
                    onSuccess = { transactionId ->
                        SnackbarService.sendSuccessMessage(MR.strings.transaction_create_success)
                        _state.update {
                            it.copy(
                                editingTransaction = null,
                                lockedFromAccount = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        SnackbarService.sendErrorMessage(error.message)
                        Napier.d(message = error.message)
                        _state.update { it.copy(isLoading = false) }
                    },
                )
            } catch (e: IllegalArgumentException) {
                // Handle validation errors from mapper (missing required fields)
                println("Transaction creation validation error: ${e.message}")
                SnackbarService.sendErrorMessage(MR.strings.transaction_create_failure)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateTransaction(data: UiTransactionData) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Convert UI transaction data to domain transaction
                val domainTransaction = data.toDomainTransaction()

                // Call update transaction use case
                updateTransactionUseCase(domainTransaction).fold(
                    onSuccess = { success ->
                        if (success) {
                            SnackbarService.sendSuccessMessage(MR.strings.transaction_update_success)
                            _state.update {
                                it.copy(
                                    editingTransaction = null,
                                    lockedFromAccount = null,
                                )
                            }
                        } else {
                            SnackbarService.sendErrorMessage(MR.strings.transaction_update_failure)
                            _state.update { it.copy(isLoading = false) }
                        }
                    },
                    onFailure = { error ->
                        SnackbarService.sendErrorMessage(MR.strings.transaction_update_failure)
                        _state.update { it.copy(isLoading = false) }
                    },
                )
            } catch (e: IllegalArgumentException) {
                // Handle validation errors from mapper (missing required fields)
                println("Transaction update validation error: ${e.message}")
                SnackbarService.sendErrorMessage(MR.strings.transaction_update_failure)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun deleteTransaction(transaction: UiTransactionDetails) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // We need to get the full domain transaction to delete
            // For now, we'll build a minimal transaction for deletion using the ID
            // The repository should handle deletion by ID
            val transactionId = transaction.id.toIntOrNull()
            if (transactionId == null) {
                SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            // Create a minimal domain transaction for deletion
            // Note: The domain transaction needs full data, so we'll need to convert the UI transaction
            try {
                val domainAccount = transaction.account.toAccount()
                val domainCategory = transaction.categories.firstOrNull()?.toCategory()

                val domainTransaction =
                    when (transaction.type) {
                        UiTransactionType.INCOME -> {
                            tech.lightfeather.domain.model.transaction.Transaction.Income(
                                incomeId = transactionId,
                                incomeName = transaction.name,
                                incomeDescription = transaction.description,
                                incomeAmount = transaction.amount.toDoubleOrNull() ?: 0.0,
                                incomeTimestamp =
                                    transaction.dateTime
                                        .toInstant(
                                            TimeZone.currentSystemDefault(),
                                        ).toEpochMilliseconds(),
                                incomeAccount = domainAccount,
                                source = domainCategory!!,
                                incomeAttachments = emptyList(),
                            )
                        }

                        UiTransactionType.EXPENSE -> {
                            tech.lightfeather.domain.model.transaction.Transaction.Expense(
                                expenseId = transactionId,
                                expenseName = transaction.name,
                                expenseDescription = transaction.description,
                                expenseAmount = transaction.amount.toDoubleOrNull() ?: 0.0,
                                expenseTimestamp =
                                    transaction.dateTime
                                        .toInstant(TimeZone.currentSystemDefault())
                                        .toEpochMilliseconds(),
                                expenseAccount = domainAccount,
                                categories = listOfNotNull(domainCategory),
                                expenseAttachments = emptyList(),
                            )
                        }

                        UiTransactionType.TRANSFER -> {
                            val receiverAccount = transaction.receiverAccount?.toAccount() ?: domainAccount
                            tech.lightfeather.domain.model.transaction.Transaction.Transfer(
                                transferId = transactionId,
                                transferName = transaction.name,
                                transferDescription = transaction.description,
                                transferAmount = transaction.amount.toDoubleOrNull() ?: 0.0,
                                transferTimestamp =
                                    transaction.dateTime
                                        .toInstant(
                                            kotlinx.datetime.TimeZone.currentSystemDefault(),
                                        ).toEpochMilliseconds(),
                                transferAccount = domainAccount,
                                receiverAccount = receiverAccount,
                                fee = transaction.transferFee?.toDoubleOrNull() ?: 0.0,
                                transferAttachments = emptyList(),
                            )
                        }
                    }

                deleteTransactionUseCase(domainTransaction.id.toLong()).fold(
                    onSuccess = { success ->
                        if (success) {
                            SnackbarService.sendSuccessMessage(MR.strings.transaction_delete_success)
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    selectedTransaction = null,
                                )
                            }
                        } else {
                            SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                            _state.update { it.copy(isLoading = false) }
                        }
                    },
                    onFailure = { error ->
                        SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                        _state.update { it.copy(isLoading = false) }
                    },
                )
            } catch (e: IllegalArgumentException) {
                // Handle validation errors during transaction reconstruction
                println("Transaction deletion error: ${e.message}")
                SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun duplicateTransaction(transaction: UiTransactionDetails) {
        _state.update {
            it.copy(editingTransaction = transaction.copy(id = ""))
        }
    }

    /**
     * Pick images using FileKit and return them — does NOT update state.
     * Use this from composables that manage their own attachment list locally.
     */
    @Suppress("TooGenericExceptionCaught") // General error handling for FileKit operations
    suspend fun pickImagesAndReturn(): List<UiAttachment> =
        try {
            val files =
                FileKit.openFilePicker(
                    type = FileKitType.Image,
                    mode = FileKitMode.Multiple(),
                )
            if (files != null) {
                val results = mutableListOf<UiAttachment>()
                files.forEachIndexed { idx, file ->
                    try {
                        val bytes = file.readBytes()
                        val compressedBytes = FileKitHelper.compressImage(bytes)
                        if (FileKitHelper.isValidImage(compressedBytes, file.mimeType())) {
                            results.add(
                                UiAttachment(
                                    id = idx.times(-1).toString(),
                                    name = file.name,
                                    mimeType = "image/jpeg",
                                    fileContent = compressedBytes,
                                ),
                            )
                        }
                    } catch (e: Exception) {
                        Napier.e("Error processing image: ${file.name}", e)
                    }
                }
                results
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Napier.e("Error picking images", e)
            SnackbarService.sendErrorMessage(MR.strings.unknown_error)
            emptyList()
        }

    /**
     * Pick images using FileKit and compress them
     */
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

                    files.forEachIndexed { idx, file ->
                        try {
                            val bytes = file.readBytes()
                            val compressedBytes = FileKitHelper.compressImage(bytes)

                            if (FileKitHelper.isValidImage(compressedBytes, file.mimeType())) {
                                newAttachments.add(
                                    UiAttachment(
                                        id = idx.times(-1).toString(), // New attachment, will be assigned ID on save
                                        name = file.name,
                                        mimeType = "image/jpeg",
                                        fileContent = compressedBytes,
                                    ),
                                )
                            }
                        } catch (e: Exception) {
                            Napier.e("Error processing image: ${file.name}", e)
                        }
                    }

                    _state.update {
                        it.copy(
                            selectedAttachments = it.selectedAttachments + newAttachments,
                        )
                    }
                }
            } catch (e: Exception) {
                Napier.e("Error picking images", e)
                SnackbarService.sendErrorMessage(MR.strings.unknown_error)
            }
        }
    }

    /**
     * Delete an attachment from the selected list
     */
    private fun deleteAttachment(attachment: UiAttachment) {
        _state.update {
            it.copy(
                selectedAttachments = it.selectedAttachments - attachment,
            )
        }
    }

    /**
     * Load attachments for a specific transaction
     */
    private fun loadAttachments(transactionId: String) {
        viewModelScope.launch {
            val id = transactionId.toIntOrNull() ?: return@launch

            attachmentRepository.getAttachmentsByTransactionId(id).fold(
                onSuccess = { attachments ->
                    val uiAttachments = attachments.map { it.toUiAttachment() }
                    _state.update {
                        it.copy(
                            transactionAttachments =
                                it.transactionAttachments + (transactionId to uiAttachments),
                        )
                    }
                },
                onFailure = { error ->
                    Napier.e("Error loading attachments for transaction $transactionId $error")
                },
            )
        }
    }

    /**
     * Select currency for wealth calculation
     */
    private fun selectCurrency(currency: UiCurrency?) {
        _state.update { it.copy(selectedCurrency = currency) }
    }

    /**
     * Load wealth worth listening to currency changes
     */
    fun loadWealthWorthListening() {
        val selectedCurrencyFlow = _state.map { it.selectedCurrency }

        // Total Amount Calculation
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            // 1. Get the Raw Accounts Flow (Source of Truth)
            val rawAccountsFlow =
                getAccountsUseCase().foldResult(
                    onSuccess = { it },
                    onFailure = { flowOf(emptyList()) },
                )

            // 2. Get the Wealth/Total flow
            val wealthInAllCurrenciesFlow =
                getWealthWorthInCurrency().foldResult(
                    onSuccess = { it },
                    onFailure = { flowOf(emptyList()) },
                )

            // 3. Get Exchange Rates
            val exchangeRatesFlow =
                exchangeRates().foldResult(
                    onSuccess = { it },
                    onFailure = { flowOf(emptyList()) },
                )

            launch {
                combine(selectedCurrencyFlow, wealthInAllCurrenciesFlow) { selected, wealth ->
                    selected to wealth
                }.collect { (selectedCurrency, wealthList) ->
                    val selectedCurrencyWealth =
                        wealthList.find { it.currency.toUiCurrency() == selectedCurrency }
                            ?: wealthList.firstOrNull()

                    _state.value =
                        _state.value.copy(
                            totalAmountInSelectedOrDefaultCurrency = (selectedCurrencyWealth?.worth ?: 0.0).toString(),
                        )
                }
            }
            launch {
                combine(rawAccountsFlow, selectedCurrencyFlow, exchangeRatesFlow) { accounts, selectedCurrency, rates ->
                    Triple(accounts, selectedCurrency, rates)
                }.distinctUntilChanged().collectLatest { (accounts, selectedCurrency, rates) ->

                    val mappedAccounts =
                        if (selectedCurrency == null) {
                            // If no currency selected, just show original balances
                            accounts.map { it.toUiBankAccount() }
                        } else {
                            // Convert ALWAYS from the original account balance
                            accounts.map { account ->
                                val uiAccount = account.toUiBankAccount()
                                val rateEntry =
                                    rates.find {
                                        it.from.id.toString() == uiAccount.currency.id &&
                                            it.to.id.toString() == selectedCurrency.id
                                    }

                                uiAccount.copy(
                                    balance = (uiAccount.balance.toDouble() * (rateEntry?.rate ?: 1.0)).toString(),
                                    currency = selectedCurrency,
                                )
                            }
                        }

                    // Update the state with a fresh Flow of the calculated list
                    _state.value = _state.value.copy(accounts = flowOf(mappedAccounts))
                }
            }
        }
    }
}
