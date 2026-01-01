package com.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.masarify.mappers.toAccount
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BankAccountsPageViewModel(
    private val navigator: Navigator,
    private val getAllAccounts: GetAllAccounts,
    private val getAllCurrencies: GetAllCurrencies,
    private val getWealthWorthInCurrency: GetWealthWorthInCurrency,
    private val exchangeRates: GetAllCurrenciesExchangeRates,
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
            }

            is BankAccountsPageIntent.TransferFromAccount -> {
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
        }
    }

    fun loadWealthWorthListening() {
        val selectedCurrencyFlow = _state.map { it.selectedCurrency }
        val wealthInAllCurrenciesFlow =
            getWealthWorthInCurrency().foldResult(
                onSuccess = { wealthFlow -> wealthFlow },
                onFailure = { error -> flowOf() },
            )
        val selectedCurrencyAndWealthFlow =
            selectedCurrencyFlow
                .combine(wealthInAllCurrenciesFlow) { selectedCurrency, wealthInAllCurrencies ->
                    selectedCurrency to wealthInAllCurrencies
                }

        val exchangeRates =
            exchangeRates().foldResult(
                onSuccess = { exchangeRatesFlow -> exchangeRatesFlow },
                onFailure = { error -> flowOf() },
            )

        val exchangeRatesAndPageStateFlow =
            exchangeRates.combine(selectedCurrencyFlow) { exchangeRates, state ->
                exchangeRates to state
            }

        viewModelScope.launch(Dispatchers.IoDispatcher) {
            selectedCurrencyAndWealthFlow.collect { (selectedCurrency, wealthInAllCurrencies) ->
                val selectedCurrencyWealth =
                    wealthInAllCurrencies.find { it.currency.toUiCurrency() == selectedCurrency }
                        ?: wealthInAllCurrencies.firstOrNull()
                _state.value =
                    _state.value.copy(
                        totalAmountInSelectedOrDefaultCurrency = (selectedCurrencyWealth?.worth ?: 0.0).toString(),
                    )
            }
        }
        viewModelScope.launch {
            exchangeRatesAndPageStateFlow.collect { (exchangeRates, selectedCurrency) ->
                if (selectedCurrency == null) {
                    _state.value =
                        _state.value.copy(
                            bankAccounts =
                                getAllAccounts().foldResult(
                                    onSuccess = { accountsFlow ->
                                        accountsFlow.map { it.map { it.toUiBankAccount() } }
                                    },
                                    onFailure = { error -> flowOf() },
                                ),
                        )
                    return@collect
                }

                val accountsWithEquivalentAmounts =
                    _state.value.bankAccounts.map { accountsFlow ->
                        accountsFlow.map { account ->
                            val selectedCurrencyExchangeRate =
                                exchangeRates.find { it.from == account.currency && it.to == selectedCurrency }
                            account.copy(
                                balance =
                                    (
                                        account.balance.toDouble() * (
                                            selectedCurrencyExchangeRate?.rate
                                                ?: 1.0
                                            )
                                        ).toString(),
                                currency = selectedCurrency,
                            )
                        }
                    }
                _state.value =
                    _state.value.copy(
                        bankAccounts = accountsWithEquivalentAmounts,
                    )
            }
        }
    }
}
