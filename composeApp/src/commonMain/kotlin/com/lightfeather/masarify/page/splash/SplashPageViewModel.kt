package com.lightfeather.masarify.page.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import com.lightfeather.masarify.navigation.routes.OnBoardingRoute
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashPageViewModel(
    private val navigator: Navigator,
    private val userRepository: UserRepository,
    private val bankAccountRepository: AccountRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashPageState())
    internal val state: StateFlow<SplashPageState> = _state

    init {
        onIntent(SplashPageIntent.NavigateToStart)
    }

    internal fun onIntent(intent: SplashPageIntent) {
        when (intent) {
            SplashPageIntent.NavigateToStart -> {
                viewModelScope.launch {
                    Napier.d("SplashPageViewModel: NavigateToStart")
                    delay(3000)
                    val isUserDataInitialized =
                        userRepository.getUserData().foldResult(
                            onSuccess = { it != null },
                            onFailure = { false },
                        )
                    val hasBankAccountsAndUserData: Flow<Boolean> =
                        bankAccountRepository.getAccounts().foldResult(
                            onSuccess = { it.map { it.isNotEmpty() && isUserDataInitialized } },
                            onFailure = { flowOf(false) },
                        )
                    Napier.d("isUserDataInitialized: $isUserDataInitialized")
                    hasBankAccountsAndUserData.collect { hasBankAccounts ->
                        Napier.d("hasBankAccounts: $hasBankAccounts")
                        if (hasBankAccounts) {
                            navigator.navigateAndClearBackStack(DashboardRoute)
                        } else {
                            navigator.navigateAndClearBackStack(OnBoardingRoute)
                        }
                    }
                }
            }
        }
    }
}
