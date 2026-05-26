@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.auth.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.navigation.routes.ForgotPasswordRoute
import tech.lightfeather.masarify.navigation.routes.RegisterRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.FakeAuthRepository
import tech.lightfeather.masarify.test.buildLoginViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginPageViewModelTest {
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
    fun updateEmailIntentUpdatesStateEmail() =
        runTest {
            val vm = buildLoginViewModel()

            vm.onIntent(LoginPageIntent.UpdateEmail("test@example.com"))

            assertEquals("test@example.com", vm.state.value.email)
        }

    @Test
    fun updatePasswordIntentUpdatesStatePassword() =
        runTest {
            val vm = buildLoginViewModel()

            vm.onIntent(LoginPageIntent.UpdatePassword("secret123"))

            assertEquals("secret123", vm.state.value.password)
        }

    @Test
    fun submitWithBlankEmailDoesNotCallLogin() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildLoginViewModel(authRepository = authRepo)

            vm.onIntent(LoginPageIntent.Submit) // email blank → early return

            assertEquals(0, authRepo.loginCallCount)
        }

    @Test
    fun submitWithBlankPasswordDoesNotCallLogin() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildLoginViewModel(authRepository = authRepo)
            vm.onIntent(LoginPageIntent.UpdateEmail("user@example.com"))

            vm.onIntent(LoginPageIntent.Submit) // password blank → early return

            assertEquals(0, authRepo.loginCallCount)
        }

    @Test
    fun navigateToRegisterIntentCallsNavigateWithRegisterRoute() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildLoginViewModel(navigator = navigator)

            vm.onIntent(LoginPageIntent.NavigateToRegister)

            assertTrue(navigator.navigatedRoutes.isNotEmpty())
            assertEquals(RegisterRoute, navigator.navigatedRoutes.first())
        }

    @Test
    fun navigateToForgotPasswordIntentCallsNavigateWithForgotPasswordRoute() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildLoginViewModel(navigator = navigator)

            vm.onIntent(LoginPageIntent.NavigateToForgotPassword)

            assertTrue(navigator.navigatedRoutes.isNotEmpty())
            assertEquals(ForgotPasswordRoute, navigator.navigatedRoutes.first())
        }
}
