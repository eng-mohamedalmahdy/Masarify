package com.lightfeather.masarify.page.bankaccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.DeleteAccount
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.UpdateAccount
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.mappers.toAccount
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.mappers.toUiCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BankAccountsPageViewModel(
    private val getAllAccounts: GetAllAccounts,
    private val createAccount: CreateAccount,
    private val deleteAccount: DeleteAccount,
    private val updateAccount: UpdateAccount,
    private val getAllCurrencies: GetAllCurrencies,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BankAccountsPageState(
            bankAccounts = getAllAccounts().foldResult(
                onSuccess = { accountsFlow -> accountsFlow.map { accounts -> accounts.map { account -> account.toUiBankAccount() } } },
                onFailure = { error -> flowOf() }
            ),
            userAccountsCurrencies = getAllCurrencies().foldResult(
                onSuccess = { currencies -> currencies.map { currencies -> currencies.map { currency -> currency.toUiCurrency() } } },
                onFailure = { error -> flowOf() }
            ),
            defaultCurrency = getAllCurrencies().foldResult(
                onSuccess = { currencies -> currencies.map { currencies -> currencies.firstOrNull()?.toUiCurrency() } },
                onFailure = { error -> flowOf(null) }
            )
        ),
    )
    internal val state: StateFlow<BankAccountsPageState> = _state

    internal fun onIntent(intent: BankAccountsPageIntent) {
        when (intent) {
            is BankAccountsPageIntent.AddBankAccount -> viewModelScope.launch {

            }

            is BankAccountsPageIntent.DeleteBankAccount -> viewModelScope.launch {

            }

            is BankAccountsPageIntent.UpdateBankAccount -> viewModelScope.launch {

            }

            is BankAccountsPageIntent.CreateTransactionInAccount -> {

            }
            is BankAccountsPageIntent.TransferFromAccount -> {

            }
            is BankAccountsPageIntent.SelectCurrency -> {
                _state.value = _state.value.copy(selectedCurrency = intent.currency)
            }

        }
    }
}
