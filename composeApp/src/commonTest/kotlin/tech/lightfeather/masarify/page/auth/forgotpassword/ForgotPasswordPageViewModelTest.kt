@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.auth.forgotpassword

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.FakeAuthRepository
import tech.lightfeather.masarify.test.buildForgotPasswordViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForgotPasswordPageViewModelTest {
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
            val vm = buildForgotPasswordViewModel()

            vm.onIntent(ForgotPasswordPageIntent.UpdateEmail("x@y.com"))

            assertEquals("x@y.com", vm.state.value.email)
        }

    @Test
    fun submitWithBlankEmailDoesNotCallForgotPassword() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildForgotPasswordViewModel(authRepository = authRepo)

            vm.onIntent(ForgotPasswordPageIntent.Submit) // email blank → early return

            assertEquals(0, authRepo.forgotPasswordCallCount)
        }

    @Test
    fun initialStateHasIsSentFalse() =
        runTest {
            val vm = buildForgotPasswordViewModel()

            assertFalse(vm.state.value.isSent)
        }

    @Test
    fun initialStateHasIsLoadingFalse() =
        runTest {
            val vm = buildForgotPasswordViewModel()

            assertFalse(vm.state.value.isLoading)
        }

    @Test
    fun navigateBackIntentCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildForgotPasswordViewModel(navigator = navigator)

            vm.onIntent(ForgotPasswordPageIntent.NavigateBack)

            assertTrue(navigator.navigateUpCallCount == 1)
        }
}
