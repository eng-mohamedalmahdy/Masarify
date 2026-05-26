@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.auth.verifyemail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildVerifyEmailViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerifyEmailPageViewModelTest {
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
    fun initialStateHasIsLoadingTrue() =
        runTest {
            val vm = buildVerifyEmailViewModel()

            assertTrue(vm.state.value.isLoading)
        }

    @Test
    fun initialStateHasIsSuccessAndIsErrorFalse() =
        runTest {
            val vm = buildVerifyEmailViewModel()

            assertFalse(vm.state.value.isSuccess)
            assertFalse(vm.state.value.isError)
        }

    @Test
    fun goToDashboardIntentNavigatesClearingBackStack() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildVerifyEmailViewModel(navigator = navigator)

            vm.onIntent(VerifyEmailPageIntent.GoToDashboard)

            assertTrue(navigator.clearedAndNavigatedRoutes.isNotEmpty())
            assertTrue(navigator.clearedAndNavigatedRoutes.contains(DashboardRoute))
        }
}
