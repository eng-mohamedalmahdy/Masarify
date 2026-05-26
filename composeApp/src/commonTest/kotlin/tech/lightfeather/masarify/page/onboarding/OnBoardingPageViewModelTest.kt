@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.onboarding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.navigation.routes.LoginRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildOnBoardingViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnBoardingPageViewModelTest {
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
    fun updateUserNameIntentUpdatesStateUserName() =
        runTest {
            val vm = buildOnBoardingViewModel()

            vm.onIntent(OnBoardingPageIntent.UpdateUserName("Alice"))

            assertEquals("Alice", vm.state.value.userName)
        }

    @Test
    fun updateAccountBalanceIntentUpdatesStateBalance() =
        runTest {
            val vm = buildOnBoardingViewModel()

            vm.onIntent(OnBoardingPageIntent.UpdateAccountBalance("1500"))

            assertEquals("1500", vm.state.value.accountBalance)
        }

    @Test
    fun navigateToSignInNavigatesToLoginRoute() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildOnBoardingViewModel(navigator = navigator)

            vm.onIntent(OnBoardingPageIntent.NavigateToSignIn)

            assertTrue(navigator.navigatedRoutes.filterIsInstance<LoginRoute>().isNotEmpty())
        }
}
