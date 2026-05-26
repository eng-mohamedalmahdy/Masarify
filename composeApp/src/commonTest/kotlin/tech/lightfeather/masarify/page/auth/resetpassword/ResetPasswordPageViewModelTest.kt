@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.auth.resetpassword

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.navigation.routes.LoginRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.FakeAuthRepository
import tech.lightfeather.masarify.test.buildResetPasswordViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResetPasswordPageViewModelTest {
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
    fun updateNewPasswordIntentUpdatesState() =
        runTest {
            val vm = buildResetPasswordViewModel()

            vm.onIntent(ResetPasswordPageIntent.UpdateNewPassword("newpass"))

            assertEquals("newpass", vm.state.value.newPassword)
        }

    @Test
    fun updateConfirmPasswordIntentUpdatesState() =
        runTest {
            val vm = buildResetPasswordViewModel()

            vm.onIntent(ResetPasswordPageIntent.UpdateConfirmPassword("newpass"))

            assertEquals("newpass", vm.state.value.confirmPassword)
        }

    @Test
    fun submitWithBlankPasswordDoesNotCallReset() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildResetPasswordViewModel(authRepository = authRepo)

            vm.onIntent(ResetPasswordPageIntent.Submit("token")) // password blank → early return

            assertEquals(0, authRepo.resetPasswordCallCount)
        }

    @Test
    fun submitWithPasswordMismatchDoesNotCallReset() =
        runTest {
            val authRepo = FakeAuthRepository()
            val vm = buildResetPasswordViewModel(authRepository = authRepo)
            vm.onIntent(ResetPasswordPageIntent.UpdateNewPassword("pass123"))
            vm.onIntent(ResetPasswordPageIntent.UpdateConfirmPassword("different"))

            vm.onIntent(ResetPasswordPageIntent.Submit("token"))

            assertEquals(0, authRepo.resetPasswordCallCount)
        }

    @Test
    fun navigateToLoginIntentNavigatesClearingBackStack() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildResetPasswordViewModel(navigator = navigator)

            vm.onIntent(ResetPasswordPageIntent.NavigateToLogin)

            assertTrue(navigator.clearedAndNavigatedRoutes.isNotEmpty())
            assertTrue(navigator.clearedAndNavigatedRoutes.contains(LoginRoute))
        }
}
