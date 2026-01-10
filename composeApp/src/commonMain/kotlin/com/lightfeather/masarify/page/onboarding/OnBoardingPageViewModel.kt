package com.lightfeather.masarify.page.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.UpsertUserData
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnBoardingPageViewModel(
    private val createCurrency: CreateCurrency,
    private val createAccount: CreateAccount,
    private val upsertUserData: UpsertUserData,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingPageState())
    internal val state: StateFlow<OnBoardingPageState> = _state

    internal fun onIntent(intent: OnBoardingPageIntent) {
        when (intent) {
            is OnBoardingPageIntent.UpdateAccountName -> _state.value = _state.value.copy(accountName = intent.name)
            is OnBoardingPageIntent.UpdateCurrencyName ->
                _state.value =
                    _state.value.copy(accountCurrencyName = intent.name)

            is OnBoardingPageIntent.UpdateCurrencySymbol ->
                _state.value =
                    _state.value.copy(mainAccountCurrencySymbol = intent.name)

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

                viewModelScope.launch {
                    val createAccountJob =
                        async(Dispatchers.IoDispatcher) {
                            val toBeCreateCurrency =
                                Currency(
                                    stateSnapshot.accountCurrencyName,
                                    stateSnapshot.mainAccountCurrencySymbol.takeIf { it.isNotBlank() }
                                        ?: stateSnapshot.accountCurrencyName,
                                )
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
                                DomainResult.Success(Unit)
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
        }
    }
}
