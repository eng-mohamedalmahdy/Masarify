package com.lightfeather.masarify.page.createbankaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.UpdateAccount
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.mappers.toCurrency
import com.lightfeather.masarify.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateBankAccountPageViewModel(
    private val account: UiBankAccount?,
    private val navigator: Navigator,
    private val createAccount: CreateAccount,
    private val updateAccount: UpdateAccount,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            CreateBankAccountPageState(
                accountId = account?.id,
                name = account?.name ?: "",
                description = account?.description ?: "",
                initialBalance = account?.balance ?: "",
                color = account?.color ?: "#FFFFFF",
                logo = account?.image ?: "",
                currency = account?.currency,
            ),
        )
    internal val state: StateFlow<CreateBankAccountPageState> = _state

    internal fun onIntent(intent: CreateBankAccountPageIntent) {
        when (intent) {
            is CreateBankAccountPageIntent.UpdateName -> {
                _state.value = _state.value.copy(name = intent.name)
            }

            is CreateBankAccountPageIntent.UpdateDescription -> {
                _state.value = _state.value.copy(description = intent.description)
            }

            is CreateBankAccountPageIntent.UpdateInitialBalance -> {
                _state.value = _state.value.copy(initialBalance = intent.balance)
            }

            is CreateBankAccountPageIntent.UpdateColor -> {
                _state.value = _state.value.copy(color = intent.color)
            }

            is CreateBankAccountPageIntent.UpdateLogo -> {
                _state.value = _state.value.copy(logo = intent.logo)
            }

            is CreateBankAccountPageIntent.UpdateCurrency -> {
                _state.value = _state.value.copy(currency = intent.currency)
            }

            is CreateBankAccountPageIntent.NavigateBack -> {
                navigator.navigateUp()
            }

            is CreateBankAccountPageIntent.Submit -> {
                submitAccount()
            }
        }
    }

    private fun submitAccount() {
        val stateSnapshot = _state.value

        // Validation
        if (stateSnapshot.name.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.account_name_required)
            return
        }

        if (stateSnapshot.currency == null) {
            SnackbarService.sendErrorMessage(MR.strings.account_currency_required)
            return
        }

        if (!stateSnapshot.inEditMode && stateSnapshot.initialBalance.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.account_balance_required)
            return
        }

        val balance = stateSnapshot.initialBalance.toDoubleOrNull()
        if (!stateSnapshot.inEditMode && balance == null) {
            SnackbarService.sendErrorMessage(MR.strings.account_balance_invalid)
            return
        }

        _state.value = _state.value.copy(isLoading = true)

        val resultAccount =
            Account(
                id = stateSnapshot.accountId?.toInt() ?: -1,
                name = stateSnapshot.name.trim(),
                description = stateSnapshot.description.trim().takeIf { it.isNotEmpty() },
                balance = balance ?: 0.0,
                color = stateSnapshot.color,
                logo = stateSnapshot.logo,
                currency = stateSnapshot.currency.toCurrency(),
            )

        viewModelScope.launch {
            try {
                if (stateSnapshot.inEditMode) {
                    updateAccount.invoke(resultAccount).fold(
                        onSuccess = {
                            SnackbarService.sendSuccessMessage(MR.strings.account_update_success)
                            navigator.navigateUp()
                        },
                        onFailure = { error ->
                            SnackbarService.sendErrorMessage(MR.strings.account_update_failure)
                        },
                    )
                } else {
                    createAccount.invoke(resultAccount).fold(
                        onSuccess = {
                            SnackbarService.sendSuccessMessage(MR.strings.account_create_success)
                            navigator.navigateUp()
                        },
                        onFailure = { error ->
                            SnackbarService.sendErrorMessage(MR.strings.account_create_failure)
                        },
                    )
                }
            } catch (e: Exception) {
                SnackbarService.sendErrorMessage(MR.strings.unknown_error)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
