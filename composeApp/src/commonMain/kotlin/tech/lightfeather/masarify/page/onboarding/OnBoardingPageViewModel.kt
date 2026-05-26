package tech.lightfeather.masarify.page.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.BankName
import tech.lightfeather.domain.model.DomainResult.Success
import tech.lightfeather.domain.model.UserData
import tech.lightfeather.domain.usecase.BankNameUseCase
import tech.lightfeather.domain.usecase.CreateAccount
import tech.lightfeather.domain.usecase.CreateCurrency
import tech.lightfeather.domain.usecase.GetAllCurrencies
import tech.lightfeather.domain.usecase.MarkOnboardingComplete
import tech.lightfeather.domain.usecase.SetDefaultAccount
import tech.lightfeather.domain.usecase.UpsertUserData
import tech.lightfeather.masarify.mappers.toCurrency
import tech.lightfeather.masarify.mappers.toUiBankName
import tech.lightfeather.masarify.mappers.toUiCurrency
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.LoginRoute

@Suppress("LongParameterList") // ViewModel DI constructor — params are injected, not caller-facing
class OnBoardingPageViewModel(
    private val createCurrency: CreateCurrency,
    private val createAccount: CreateAccount,
    private val upsertUserData: UpsertUserData,
    private val navigator: Navigator,
    private val getAllCurrencies: GetAllCurrencies,
    private val getAllBankNames: BankNameUseCase.GetAllBankNames,
    private val createBankName: BankNameUseCase.CreateBankName,
    private val setDefaultAccount: SetDefaultAccount,
    private val markOnboardingComplete: MarkOnboardingComplete,
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingPageState())
    internal val state: StateFlow<OnBoardingPageState> = _state

    init {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getAllCurrencies().foldSuspend(
                onSuccess = { currenciesFlow ->
                    val uiFlow = currenciesFlow.map { it.map { it.toUiCurrency() } }
                    uiFlow.collect {
                        _state.update { state ->
                            state.copy(
                                appCurrencies = it,
                                selectedCurrency = it.firstOrNull { c -> c.symbol == "E£" },
                            )
                        }
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(it.message)
                },
            )
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            getAllBankNames().foldSuspend(
                onSuccess = { bankNamesFlow ->
                    bankNamesFlow.map { it.map { it.toUiBankName() } }.collect { banks ->
                        _state.update { it.copy(availableBanks = banks, selectedBank = banks.firstOrNull()) }
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                },
            )
        }
    }

    @Suppress("CyclomaticComplexMethod") // Intent handler with multiple branches — complexity is acceptable
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

            is OnBoardingPageIntent.SelectBank -> {
                _state.value =
                    _state.value.copy(
                        selectedBank = intent.bank,
                        accountLogo = intent.bank.logoUrl.orEmpty(),
                    )
            }

            is OnBoardingPageIntent.AddNewBank -> {
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
                                    accountLogo = "",
                                    availableBanks = _state.value.availableBanks + uiBankName,
                                )
                        },
                        onFailure = {
                            SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                        },
                    )
                }
            }

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
                            val currencyIdResult =
                                if (toBeCreateCurrency.id != -1) {
                                    Success(toBeCreateCurrency.id)
                                } else {
                                    createCurrency(toBeCreateCurrency)
                                }

                            currencyIdResult.flatMapSuspend { currencyId ->
                                val toBeCreateAccount =
                                    Account(
                                        name = stateSnapshot.selectedBank?.name.orEmpty(),
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
                            upsertUserDataResult.flatMap {
                                Success(accountId)
                            }
                        }.foldSuspend(
                            onSuccess = { accountId ->
                                setDefaultAccount(accountId as Int)
                                markOnboardingComplete()
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

            OnBoardingPageIntent.NavigateToSignIn -> navigator.navigate(LoginRoute)
        }
    }
}
