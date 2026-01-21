package com.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.usecase.DeleteTransaction
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.domain.usecase.UpdateTransaction
import com.lightfeather.masarify.mappers.toAccount
import com.lightfeather.masarify.mappers.toTransaction
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BankAccountsPageViewModel(
    private val navigator: Navigator,
    private val getAllAccounts: GetAllAccounts,
    private val getAllCurrencies: GetAllCurrencies,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val exchangeRates: GetAllCurrenciesExchangeRates,
    private val deleteTransaction: DeleteTransaction,
    private val updateTransaction: UpdateTransaction,
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
                userAccountsCurrencies =
                    getAllCurrencies().foldResult(
                        onSuccess = { currencies ->
                            currencies.map { currencies ->
                                currencies.map { currency -> currency.toUiCurrency() }.also {
                                    Napier.d("Currencies mapped to UI: $it")
                                }
                            }
                        },
                        onFailure = { error -> flowOf() },
                    ),
                defaultCurrency =
                    getAllCurrencies().foldResult(
                        onSuccess = { currencies ->
                            currencies.map { currencies -> currencies.firstOrNull()?.toUiCurrency() }
                        },
                        onFailure = { error -> flowOf(null) },
                    ),
            ),
        )
    internal val state: StateFlow<BankAccountsPageState> = _state

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
                _state.value = _state.value.copy(showAddEditDialog = false, underProcessTransaction = null)
            }

            BankAccountsPageIntent.CancelUpdateTransaction -> {
                _state.value = _state.value.copy(showAddEditDialog = false, underProcessTransaction = null)
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

            BankAccountsPageIntent.ConfirmUpdateTransaction -> {
                viewModelScope.launch {
                    updateTransaction(_state.value.underProcessTransaction!!.toTransaction()).fold(
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
                _state.value = _state.value.copy(underProcessTransaction = intent.transaction, showAddEditDialog = true)
            }

            is BankAccountsPageIntent.UpdateTransaction -> {
                _state.value = _state.value.copy(underProcessTransaction = intent.transaction, showAddEditDialog = true)
            }

            is BankAccountsPageIntent.DuplicateTransaction -> TODO()
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

        // Total Amount Calculation
        viewModelScope.launch(Dispatchers.IoDispatcher) {
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

        // Bank Accounts List Calculation (The fix is here)
        viewModelScope.launch(Dispatchers.IoDispatcher) {
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
