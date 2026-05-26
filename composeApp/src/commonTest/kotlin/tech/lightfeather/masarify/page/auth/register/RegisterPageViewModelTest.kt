@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.auth.register

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.FakeAuthRepository
import tech.lightfeather.masarify.test.buildRegisterViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterPageViewModelTest {
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
    fun updateNameIntentUpdatesStateName() =
        runTest {
            val vm = buildRegisterViewModel()

            vm.onIntent(RegisterPageIntent.UpdateName("Alice"))

            assertEquals("Alice", vm.state.value.name)
        }

    @Test
    fun updateEmailIntentUpdatesStateEmail() =
        runTest {
            val vm = buildRegisterViewModel()

            vm.onIntent(RegisterPageIntent.UpdateEmail("a@b.com"))

            assertEquals("a@b.com", vm.state.value.email)
        }

    @Test
    fun updatePasswordIntentUpdatesStatePassword() =
        runTest {
            val vm = buildRegisterViewModel()

            vm.onIntent(RegisterPageIntent.UpdatePassword("pass123"))

            assertEquals("pass123", vm.state.value.password)
        }

    @Test
    fun updateConfirmPasswordIntentUpdatesStateConfirmPassword() =
        runTest {
            val vm = buildRegisterViewModel()

            vm.onIntent(RegisterPageIntent.UpdateConfirmPassword("pass123"))

            assertEquals("pass123", vm.state.value.confirmPassword)
        }

    @Test
    fun submitWithBlankNameDoesNotCallRegister() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildRegisterViewModel(authRepository = authRepo)

            vm.onIntent(RegisterPageIntent.Submit) // name blank → early return

            assertEquals(0, authRepo.registerCallCount)
        }

    @Test
    fun submitWithPasswordMismatchDoesNotCallRegister() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildRegisterViewModel(authRepository = authRepo)
            vm.onIntent(RegisterPageIntent.UpdateName("Alice"))
            vm.onIntent(RegisterPageIntent.UpdateEmail("a@b.com"))
            vm.onIntent(RegisterPageIntent.UpdatePassword("pass123"))
            vm.onIntent(RegisterPageIntent.UpdateConfirmPassword("different"))

            vm.onIntent(RegisterPageIntent.Submit)

            assertEquals(0, authRepo.registerCallCount)
        }

    @Test
    fun navigateToLoginIntentCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildRegisterViewModel(navigator = navigator)

            vm.onIntent(RegisterPageIntent.NavigateToLogin)

            assertTrue(navigator.navigateUpCallCount == 1)
        }
}
