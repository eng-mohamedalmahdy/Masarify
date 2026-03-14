package com.lightfeather.masarify.page.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.DomainResult.Success
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.UpsertUserData
import com.lightfeather.masarify.mappers.toCurrency
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OnBoardingPageViewModel(
    private val createCurrency: CreateCurrency,
    private val createAccount: CreateAccount,
    private val upsertUserData: UpsertUserData,
    private val navigator: Navigator,
    private val getAllCurrencies: GetAllCurrencies,
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingPageState())
    internal val state: StateFlow<OnBoardingPageState> = _state

    init {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getAllCurrencies().foldSuspend(
                onSuccess = { currenciesFlow ->
                    val uiFlow = currenciesFlow.map { it.map { it.toUiCurrency() } }
                    uiFlow.collect {
                        _state.value = _state.value.copy(appCurrencies = it)
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(it.message)
                },
            )
        }
    }

    internal fun onIntent(intent: OnBoardingPageIntent) {
        when (intent) {
            is OnBoardingPageIntent.UpdateAccountName -> _state.value = _state.value.copy(accountName = intent.name)
            is OnBoardingPageIntent.UpdateCurrency ->
                _state.value =
                    _state.value.copy(selectedCurrency = intent.currency)

            is OnBoardingPageIntent.UpdateUserName -> _state.value = _state.value.copy(userName = intent.name)
            is OnBoardingPageIntent.UpdateAccountBalance ->
                _state.value =
                    _state.value.copy(accountBalance = intent.balance)

            OnBoardingPageIntent.Submit -> {
                val stateSnapshot = _state.value
                val validationError =
                    stateSnapshot.userNameError ?: stateSnapshot.accountNameError
                        ?: stateSnapshot.currencyNameError ?: stateSnapshot.balanceError

                if (validationError != null) {
                    SnackbarService.sendErrorMessage(validationError)
                    return
                }
                if (stateSnapshot.accountBalance.toDoubleOrNull() == null) {
                    SnackbarService.sendErrorMessage(MR.strings.account_balance_invalid)
                    return
                }

                viewModelScope.launch {
                    val createAccountJob =
                        async(Dispatchers.IoDispatcher) {
                            val toBeCreateCurrency = stateSnapshot.selectedCurrency!!.toCurrency()
                            val createCurrencyResult = createCurrency(toBeCreateCurrency)

                            createCurrencyResult.flatMapSuspend { currencyId ->
                                val toBeCreateAccount =
                                    Account(
                                        name = stateSnapshot.accountName,
                                        currency = toBeCreateCurrency.copy(id = currencyId),
                                        description = "",
                                        balance = stateSnapshot.accountBalance.toDouble(),
                                        color = stateSnapshot.accountColor,
                                        logo = stateSnapshot.accountLogo,
                                    )
                                createAccount(toBeCreateAccount)
                            }
                        }
                    val upsertUserDataJob =
                        async(Dispatchers.IoDispatcher) {
                            upsertUserData(UserData(stateSnapshot.userName))
                        }
                    val (createAccountResult, upsertUserDataResult) = awaitAll(createAccountJob, upsertUserDataJob)
                    createAccountResult
                        .flatMap { accountId ->
                            upsertUserDataResult.flatMap { userDataId ->
                                Success(Unit)
                            }
                        }.fold(
                            onSuccess = {
                                navigator.navigateAndClearBackStack(DashboardRoute)
                                SnackbarService.sendSuccessMessage(MR.strings.app_slogan)
                            },
                            onFailure = { SnackbarService.sendErrorMessage(it.message) },
                        )
                }
            }

            is OnBoardingPageIntent.AddNewCurrency -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    createCurrency(intent.currency.toCurrency()).fold(
                        onSuccess = {
                            SnackbarService.sendSuccessMessage(MR.strings.currency_create_success)
                        },
                        onFailure = {
                            SnackbarService.sendErrorMessage(it.message)
                        },
                    )
                }
            }
        }
    }
}
