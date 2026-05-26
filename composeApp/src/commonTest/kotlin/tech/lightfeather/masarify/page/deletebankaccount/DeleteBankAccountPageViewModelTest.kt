@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.deletebankaccount

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildDeleteBankAccountViewModel
import tech.lightfeather.masarify.test.testAccount
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteBankAccountPageViewModelTest {
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
    fun uiAccountNameMatchesInputAccount() =
        runTest {
            val vm = buildDeleteBankAccountViewModel(account = testAccount)

            assertEquals(testAccount.name, vm.uiAccount.name)
        }

    @Test
    fun onCancelCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildDeleteBankAccountViewModel(navigator = navigator)

            vm.onCancel()

            assertTrue(navigator.navigateUpCallCount == 1)
        }

    @Test
    fun onConfirmCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildDeleteBankAccountViewModel(navigator = navigator)

            vm.onConfirm()

            assertTrue(navigator.navigateUpCallCount == 1)
        }
}
