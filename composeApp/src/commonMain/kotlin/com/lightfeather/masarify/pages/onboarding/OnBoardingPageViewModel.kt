package com.lightfeather.masarify.pages.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.UpsertUserData
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
) : ViewModel() {

    private val _state = MutableStateFlow(OnBoardingPageState())
    internal val state: StateFlow<OnBoardingPageState> = _state

    internal fun onIntent(intent: OnBoardingPageIntent) {
        when (intent) {
            is OnBoardingPageIntent.UpdateAccountName -> _state.value = _state.value.copy(accountName = intent.name)
            is OnBoardingPageIntent.UpdateCurrencyName -> _state.value =
                _state.value.copy(accountCurrencyName = intent.name)

            is OnBoardingPageIntent.UpdateCurrencySymbol -> _state.value =
                _state.value.copy(mainAccountCurrencySymbol = intent.name)

            is OnBoardingPageIntent.UpdateUserName -> _state.value = _state.value.copy(userName = intent.name)
            OnBoardingPageIntent.Submit -> viewModelScope.launch {
                val createAccountJob = async(Dispatchers.IoDispatcher) {
                    val toBeCreateCurrency = Currency(
                        _state.value.accountCurrencyName,
                        _state.value.mainAccountCurrencySymbol
                    )
                    val createCurrencyResult = createCurrency(toBeCreateCurrency)

                     createCurrencyResult.flatMapSuspend { currencyId ->
                        val toBeCreateAccount = Account(
                            name = _state.value.accountName,
                            currency = toBeCreateCurrency.copy(id = currencyId),
                            description = "",
                            balance = _state.value.accountBalance.toDouble(),
                            color = _state.value.accountColor,
                            logo = _state.value.accountLogo,
                        )
                        createAccount(toBeCreateAccount)
                    }
                }
                val upsertUserDataJob = async(Dispatchers.IoDispatcher) {
                    upsertUserData(UserData(_state.value.userName))
                }
                awaitAll(createAccountJob, upsertUserDataJob)
            }
        }
    }
}
