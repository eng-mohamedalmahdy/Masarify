package tech.lightfeather.masarify.page.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.UserRepository
import tech.lightfeather.domain.usecase.SeedApplicationData
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.Route
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.domain.usecase.AcquireAndUpdateFcmUseCase

class SplashPageViewModel(
    private val navigator: Navigator,
    private val userRepository: UserRepository,
    private val seedApplicationData: SeedApplicationData,
    private val acquireAndUpdateFcmUseCase: AcquireAndUpdateFcmUseCase,
    private val pendingRoute: Route? = null,
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

                    val isLoggedIn = userRepository.isLoggedIn()
                    Napier.d("isLoggedIn: $isLoggedIn")
                    if (isLoggedIn) {
                        if (userRepository.getFcmToken() == null) {
                            viewModelScope.launch(Dispatchers.IoDispatcher) {
                                acquireAndUpdateFcmUseCase()
                            }
                        }
                        navigator.navigateAndClearBackStack(DashboardRoute)
                        pendingRoute?.let { navigator.navigate(it) }
                    } else {
                        navigator.navigateAndClearBackStack(OnBoardingRoute)
                    }
                }
            }
        }
    }
}
