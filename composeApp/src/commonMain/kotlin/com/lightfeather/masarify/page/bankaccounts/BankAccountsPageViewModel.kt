package com.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BankAccountsPageViewModel(
    private val navigator: Navigator,
    private val getAllAccounts: GetAllAccounts,
    private val getAllCurrencies: GetAllCurrencies,
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
                                currencies.map { currency -> currency.toUiCurrency() }
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
        }
    }
}
