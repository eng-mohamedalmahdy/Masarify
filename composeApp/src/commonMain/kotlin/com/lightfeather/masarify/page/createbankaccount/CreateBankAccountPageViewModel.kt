package com.lightfeather.masarify.page.createbankaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.BankName
import com.lightfeather.domain.usecase.BankNameUseCase
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetUserSavedColors
import com.lightfeather.domain.usecase.SaveUserColor
import com.lightfeather.domain.usecase.SetDefaultAccount
import com.lightfeather.domain.usecase.UpdateAccount
import com.lightfeather.masarify.mappers.toCurrency
import com.lightfeather.masarify.mappers.toUiBankName
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class CreateBankAccountPageViewModel(
    private val account: UiBankAccount,
    private val navigator: Navigator,
    private val createAccount: CreateAccount,
    private val updateAccount: UpdateAccount,
    private val getAllCurrencies: GetAllCurrencies,
    private val getUserSavedColors: GetUserSavedColors,
    private val saveColor: SaveUserColor,
    private val createCurrency: CreateCurrency,
    private val getAllBankNames: BankNameUseCase.GetAllBankNames,
    private val createBankName: BankNameUseCase.CreateBankName,
    private val setDefaultAccount: SetDefaultAccount,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            CreateBankAccountPageState(
                accountId = account.id.takeIf { it.isNotEmpty() && account != UiBankAccount.empty },
                name = account.name,
                description = account.description.orEmpty(),
                initialBalance = account.balance,
                color = account.color.takeIf { it.isNotEmpty() } ?: "#FFFFFF",
                logo = account.image.orEmpty(),
                currency = account.currency.takeIf { it != UiCurrency.empty },
                isDefault = account.isDefault,
            ),
        )
    internal val state: StateFlow<CreateBankAccountPageState> = _state

    init {
        val userSavedColors = getUserSavedColors()
        _state.value = _state.value.copy(savedColors = userSavedColors)
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getAllCurrencies().foldSuspend(
                onSuccess = { currencies ->
                    currencies.map { it.map { it.toUiCurrency() } }.collect {
                        _state.value = _state.value.copy(availableCurrencies = it)
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.currency_fetch_failure)
                },
            )
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getAllBankNames().foldSuspend(
                onSuccess = { bankNamesFlow ->
                    bankNamesFlow.map { it.map { it.toUiBankName() } }.collect { banks ->
                        val currentLogoMatch = banks.find { it.logoUrl == _state.value.logo }
                        _state.value =
                            _state.value.copy(
                                availableBanks = banks,
                                selectedBank = currentLogoMatch ?: _state.value.selectedBank,
                            )
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                },
            )
        }
    }

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

            is CreateBankAccountPageIntent.SelectBank -> {
                _state.value =
                    _state.value.copy(
                        selectedBank = intent.bank,
                        logo = intent.bank.logoUrl.orEmpty(),
                    )
            }

            is CreateBankAccountPageIntent.AddNewBank -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    val newBank =
                        BankName(
                            name = intent.bankName,
                            resourceKey = null,
                            logoUrl = null,
                            isDefault = false,
                        )
                    createBankName(newBank).fold(
                        onSuccess = { bankId ->
                            val uiBankName = newBank.copy(id = bankId).toUiBankName()
                            _state.value =
                                _state.value.copy(
                                    selectedBank = uiBankName,
                                    logo = "",
                                    availableBanks = _state.value.availableBanks + uiBankName,
                                )
                        },
                        onFailure = {
                            SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                        },
                    )
                }
            }

            is CreateBankAccountPageIntent.ToggleDefault -> {
                _state.value = _state.value.copy(isDefault = !_state.value.isDefault)
            }

            is CreateBankAccountPageIntent.NavigateBack -> {
                navigator.navigateUp()
            }

            is CreateBankAccountPageIntent.Submit -> {
                submitAccount()
            }

            is CreateBankAccountPageIntent.AddNewCurrency -> {
                viewModelScope.launch {
                    createCurrency(intent.currency.toCurrency()).fold(
                        onSuccess = { currencyId ->
                            _state.value =
                                _state.value.copy(currency = intent.currency.copy(id = currencyId.toString()))
                        },
                        onFailure = { error ->
                            SnackbarService.sendErrorMessage(error.message)
                        },
                    )
                }
            }

            is CreateBankAccountPageIntent.SaveColor -> {
                saveColor(intent.color)
                _state.value = _state.value.copy(savedColors = getUserSavedColors())
            }
        }
    }

    private fun submitAccount() {
        val stateSnapshot = _state.value

        val validationError = validateAccountInput(stateSnapshot)
        if (validationError != null) {
            SnackbarService.sendErrorMessage(validationError)
            return
        }

        _state.value = _state.value.copy(isLoading = true)

        val resultAccount =
            Account(
                id = stateSnapshot.accountId?.toInt() ?: -1,
                name = stateSnapshot.name.trim(),
                description = stateSnapshot.description.trim().takeIf { it.isNotEmpty() },
                balance = stateSnapshot.initialBalance.toDoubleOrNull() ?: 0.0,
                color = stateSnapshot.color,
                logo = stateSnapshot.logo,
                currency = stateSnapshot.currency!!.toCurrency(),
                isDefault = stateSnapshot.isDefault,
            )

        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        viewModelScope.launch {
            try {
                if (stateSnapshot.inEditMode) {
                    updateAccount.invoke(resultAccount).foldSuspend(
                        onSuccess = {
                            if (stateSnapshot.isDefault) {
                                setDefaultAccount(stateSnapshot.accountId!!.toInt())
                            }
                            SnackbarService.sendSuccessMessage(MR.strings.account_update_success)
                            resetState()
                            navigator.navigateUp()
                        },
                        onFailure = {
                            SnackbarService.sendErrorMessage(MR.strings.account_update_failure)
                        },
                    )
                } else {
                    createAccount.invoke(resultAccount).foldSuspend(
                        onSuccess = { accountId ->
                            if (stateSnapshot.isDefault) {
                                setDefaultAccount(accountId)
                            }
                            SnackbarService.sendSuccessMessage(MR.strings.account_create_success)
                            resetState()
                            navigator.navigateUp()
                        },
                        onFailure = {
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

    private fun validateAccountInput(state: CreateBankAccountPageState): StringResource? =
        when {
            state.name.isBlank() -> MR.strings.account_name_required
            state.currency == null -> MR.strings.account_currency_required
            !state.inEditMode && state.initialBalance.isBlank() -> MR.strings.account_balance_required
            !state.inEditMode && state.initialBalance.toDoubleOrNull() == null -> MR.strings.account_balance_invalid
            else -> null
        }

    fun resetState() {
        _state.value = CreateBankAccountPageState()
    }
}
