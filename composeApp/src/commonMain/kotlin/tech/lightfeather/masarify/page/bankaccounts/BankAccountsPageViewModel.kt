package tech.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import tech.lightfeather.domain.usecase.GetUsedCurrencies
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.masarify.framework.FileKitHelper
import tech.lightfeather.masarify.mappers.toAccount
import tech.lightfeather.masarify.mappers.toDomainTransaction
import tech.lightfeather.masarify.mappers.toUiAttachment
import tech.lightfeather.masarify.mappers.toUiBankAccount
import tech.lightfeather.masarify.mappers.toUiCurrency
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BankAccountsPageViewModel(
    private val navigator: Navigator,
    private val getAllAccounts: GetAllAccounts,
    private val getAllCurrencies: GetUsedCurrencies,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val exchangeRates: GetAllCurrenciesExchangeRates,
    private val deleteTransaction: DeleteTransaction,
    private val updateTransaction: UpdateTransaction,
    private val attachmentRepository: AttachmentRepository,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            BankAccountsPageState(
                bankAccounts =
                    getAllAccounts().foldResult(
                        onSuccess = { accountsFlow ->
                            accountsFlow.map { accounts ->
                                accounts.map { account -> account.toUiBankAccount() }
                            }
                        },
                        onFailure = { error -> flowOf() },
                    ),
            ),
        )
    internal val state: StateFlow<BankAccountsPageState> = _state

    init {
        viewModelScope.launch {
            val currenciesFlow =
                getAllCurrencies().foldResult(
                    onSuccess = { currencies ->
                        currencies.map { currencies ->
                            currencies.map { currency -> currency.toUiCurrency() }.also {
                                Napier.d("Currencies mapped to UI: $it")
                            }
                        }
                    },
                    onFailure = { error -> flowOf() },
                )
            _state.value = _state.value.copy(userAccountsCurrencies = currenciesFlow)
            launch(Dispatchers.IoDispatcher) {
                currenciesFlow.collect {
                    _state.value = _state.value.copy(defaultCurrency = flowOf(it.firstOrNull()))
                }
            }
        }
    }

    @Suppress("CyclomaticComplexMethod") // Complexity due to comprehensive intent handling
    internal fun onIntent(intent: BankAccountsPageIntent) {
        when (intent) {
            is BankAccountsPageIntent.DeleteBankAccount -> {
                viewModelScope.launch {
                    // Navigate to delete dialog (main navigation, not detail pane)
                    navigator.navigate(DeleteAccountRoute(intent.account.toAccount()))
                }
            }

            is BankAccountsPageIntent.CreateTransactionInAccount -> {
                viewModelScope.launch {
                    // Navigate to transactions page with add dialog open and account locked
                    navigator.navigate(
                        TransactionsRoute(
                            openAddDialog = true,
                            fromAccountId = intent.account.id,
                        ),
                    )
                }
            }

            is BankAccountsPageIntent.TransferFromAccount -> {
                viewModelScope.launch {
                    // Navigate to transactions page with transfer dialog open and from-account locked
                    navigator.navigate(
                        TransactionsRoute(
                            openAddDialog = true,
                            transactionType = UiTransactionType.TRANSFER,
                            fromAccountId = intent.account.id,
                        ),
                    )
                }
            }

            is BankAccountsPageIntent.SelectCurrency -> {
                _state.value = _state.value.copy(selectedCurrency = intent.currency)
            }

            is BankAccountsPageIntent.LoadData -> {
                loadWealthWorthListening()
            }

            is BankAccountsPageIntent.SelectAccount -> {
                _state.value = _state.value.copy(selectedAccount = intent.account)
            }

            BankAccountsPageIntent.CancelDeleteTransaction -> {
                _state.value =
                    _state.value.copy(
                        showAddEditDialog = false,
                        underProcessTransaction = null,
                        selectedAttachments = emptyList(),
                    )
            }

            BankAccountsPageIntent.CancelUpdateTransaction -> {
                _state.value =
                    _state.value.copy(
                        showAddEditDialog = false,
                        underProcessTransaction = null,
                        selectedAttachments = emptyList(),
                    )
            }

            BankAccountsPageIntent.ConfirmDeleteTransaction -> {
                viewModelScope.launch {
                    deleteTransaction(
                        _state.value.underProcessTransaction!!
                            .id
                            .toLong(),
                    ).fold(
                        onSuccess = {
                            _state.value = _state.value.copy(showAddEditDialog = false, underProcessTransaction = null)
                            SnackbarService.sendSuccessMessage(MR.strings.transaction_delete_success)
                        },
                        onFailure = {
                            _state.value = _state.value.copy(showAddEditDialog = false)
                            SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                        },
                    )
                }
            }

            is BankAccountsPageIntent.ConfirmUpdateTransaction -> {
                viewModelScope.launch {
                    updateTransaction(intent.transactionDate.toDomainTransaction()).fold(
                        onSuccess = {
                            _state.value = _state.value.copy(showAddEditDialog = false, underProcessTransaction = null)
                            SnackbarService.sendSuccessMessage(MR.strings.transaction_update_success)
                        },
                        onFailure = {
                            _state.value = _state.value.copy(showAddEditDialog = false)
                            SnackbarService.sendErrorMessage(MR.strings.transaction_update_failure)
                        },
                    )
                }
            }

            is BankAccountsPageIntent.DeleteTransaction -> {
                loadAttachments(intent.transaction.id)
                _state.value = _state.value.copy(underProcessTransaction = intent.transaction, showAddEditDialog = true)
            }

            is BankAccountsPageIntent.UpdateTransaction -> {
                loadAttachments(intent.transaction.id)
                viewModelScope.launch {
                    kotlinx.coroutines.delay(100) // Wait for attachments to load
                    val attachments = _state.value.transactionAttachments[intent.transaction.id] ?: emptyList()
                    _state.update {
                        it.copy(
                            showAddEditDialog = true,
                            underProcessTransaction = intent.transaction,
                            selectedAttachments = attachments,
                        )
                    }
                }
            }

            is BankAccountsPageIntent.DuplicateTransaction -> TODO()

            // Attachment operations
            is BankAccountsPageIntent.PickImages -> pickImages()
            is BankAccountsPageIntent.DeleteAttachment -> deleteAttachment(intent.attachment)
            is BankAccountsPageIntent.LoadAttachments -> loadAttachments(intent.transactionId)
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
            val id = transactionId.toIntOrNull() ?: return@launch
            attachmentRepository.getAttachmentsByTransactionId(id).fold(
                onSuccess = { attachments ->
                    val uiAttachments = attachments.map { it.toUiAttachment() }
                    _state.update {
                        it.copy(transactionAttachments = it.transactionAttachments + (transactionId to uiAttachments))
                    }
                },
                onFailure = { error ->
                    Napier.e("Error loading attachments $error")
                },
            )
        }
    }

    fun loadWealthWorthListening() {
        val selectedCurrencyFlow = _state.map { it.selectedCurrency }

        // 1. Get the Raw Accounts Flow (Source of Truth)
        val rawAccountsFlow =
            getAllAccounts().foldResult(
                onSuccess = { it },
                onFailure = { flowOf(emptyList()) },
            )

        // Total Amount Calculation
        viewModelScope.launch(Dispatchers.IoDispatcher) {
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
                    _state.value = _state.value.copy(bankAccounts = flowOf(mappedAccounts))
                }
            }
        }
    }
}
