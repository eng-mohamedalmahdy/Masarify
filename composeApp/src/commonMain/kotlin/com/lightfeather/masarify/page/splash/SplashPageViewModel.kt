package com.lightfeather.masarify.page.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.SeedApplicationData
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
    private val seedApplicationData: SeedApplicationData,
) : ViewModel() {
    private val _state = MutableStateFlow(SplashPageState())
    internal val state: StateFlow<SplashPageState> = _state

    init {
        onIntent(SplashPageIntent.NavigateToStart)
    }

    // Generic exception catch is intentional to prevent any seeding failure from crashing the app
    @Suppress("TooGenericExceptionCaught")
    internal fun onIntent(intent: SplashPageIntent) {
        when (intent) {
            SplashPageIntent.NavigateToStart -> {
                viewModelScope.launch {
                    Napier.d("SplashPageViewModel: NavigateToStart")

                    // Seed application data (currencies, bank names, categories) on first launch
                    try {
                        when (val seedResult = seedApplicationData()) {
                            is DomainResult.Success -> {
                                Napier.d("Application data seeding completed successfully")
                            }
                            is DomainResult.Failure -> {
                                Napier.e("Application data seeding failed: ${seedResult.error}")
                                // Continue anyway - app will work with empty/partial data
                            }
                        }
                    } catch (e: Exception) {
                        Napier.e("Unexpected error during seeding: ${e.message}", e)
                        // Continue anyway to prevent app crash
                    }

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
