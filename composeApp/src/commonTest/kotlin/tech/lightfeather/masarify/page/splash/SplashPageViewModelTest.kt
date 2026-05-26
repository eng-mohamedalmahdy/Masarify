@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildSplashViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SplashPageViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun navigatesToOnboardingWhenNotLoggedInAndNotOnboarded() =
        runTest(testDispatcher) {
            val navigator = CapturingNavigator()
            buildSplashViewModel(navigator = navigator, isLoggedIn = false, isOnboardingComplete = false)
            advanceUntilIdle()
            assertTrue(navigator.clearedAndNavigatedRoutes.filterIsInstance<OnBoardingRoute>().isNotEmpty())
        }

    @Test
    fun navigatesToDashboardWhenLoggedIn() =
        runTest(testDispatcher) {
            val navigator = CapturingNavigator()
            buildSplashViewModel(navigator = navigator, isLoggedIn = true, isOnboardingComplete = false)
            advanceUntilIdle()
            assertTrue(navigator.clearedAndNavigatedRoutes.filterIsInstance<DashboardRoute>().isNotEmpty())
        }

    @Test
    fun navigatesToDashboardWhenOnboardingComplete() =
        runTest(testDispatcher) {
            val navigator = CapturingNavigator()
            buildSplashViewModel(navigator = navigator, isLoggedIn = false, isOnboardingComplete = true)
            advanceUntilIdle()
            assertTrue(navigator.clearedAndNavigatedRoutes.filterIsInstance<DashboardRoute>().isNotEmpty())
        }
}
