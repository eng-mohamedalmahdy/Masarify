package com.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
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
            is BankAccountsPageIntent.AddBankAccount ->
                viewModelScope.launch {
                }

            is BankAccountsPageIntent.DeleteBankAccount ->
                viewModelScope.launch {
                }

            is BankAccountsPageIntent.UpdateBankAccount ->
                viewModelScope.launch {
                }

            is BankAccountsPageIntent.CreateTransactionInAccount -> {
            }

            is BankAccountsPageIntent.TransferFromAccount -> {
            }

            is BankAccountsPageIntent.SelectCurrency -> {
                _state.value = _state.value.copy(selectedCurrency = intent.currency)
            }

            is BankAccountsPageIntent.SelectAccount ->
                _state.value =
                    _state.value.copy(selectedAccount = intent.account)

            BankAccountsPageIntent.CreateBankAccount -> {

            }

            is BankAccountsPageIntent.LoadData -> {
                loadWealthWorthListening()
            }
        }
    }

    fun loadWealthWorthListening() {
        val selectedCurrencyFlow = _state.map { it.selectedCurrency }
        val wealthInAllCurrenciesFlow = getWealthWorthInCurrency().foldResult(
            onSuccess = { wealthFlow -> wealthFlow },
            onFailure = { error -> flowOf() }
        )
        val selectedCurrencyAndWealthFlow = selectedCurrencyFlow
            .combine(wealthInAllCurrenciesFlow) { selectedCurrency, wealthInAllCurrencies ->
                selectedCurrency to wealthInAllCurrencies
            }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            selectedCurrencyAndWealthFlow.collect { (selectedCurrency, wealthInAllCurrencies) ->
                val selectedCurrencyWealth = wealthInAllCurrencies.find { it.currency == selectedCurrency }
                    ?: wealthInAllCurrencies.firstOrNull()
                _state.value = _state.value.copy(
                    totalAmountInSelectedOrDefaultCurrency = (selectedCurrencyWealth?.worth ?: 0.0).toString()
                )
            }
        }
    }
}
