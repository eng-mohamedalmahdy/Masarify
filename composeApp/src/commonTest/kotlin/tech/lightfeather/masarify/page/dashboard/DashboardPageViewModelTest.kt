@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.dashboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.masarify.test.buildDashboardViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

class DashboardPageViewModelTest {
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
    fun dismissBiometricSuggestionSetsShowBiometricSuggestionFalse() =
        runTest {
            val vm = buildDashboardViewModel()

            vm.onIntent(DashboardPageIntent.DismissBiometricSuggestion)

            assertFalse(vm.state.value.showBiometricSuggestion)
        }

    @Test
    fun cancelUpdateTransactionHidesDialogAndClearsTransaction() =
        runTest {
            val vm = buildDashboardViewModel()

            vm.onIntent(DashboardPageIntent.CancelUpdateTransaction)

            assertFalse(vm.state.value.showAddEditDialog)
            assertNull(vm.state.value.underProcessTransaction)
        }

    @Test
    fun dismissStartOverDialogSetsShowStartOverDialogFalse() =
        runTest {
            val vm = buildDashboardViewModel()

            vm.onIntent(DashboardPageIntent.DismissStartOverDialog)

            assertFalse(vm.state.value.showStartOverDialog)
        }

    @Test
    fun dismissFixBalanceDialogSetsShowFixBalanceDialogFalse() =
        runTest {
            val vm = buildDashboardViewModel()

            vm.onIntent(DashboardPageIntent.DismissFixBalanceDialog)

            assertFalse(vm.state.value.showFixBalanceDialog)
            assertNull(vm.state.value.fixBalanceAccount)
        }

    @Test
    fun selectAccountUpdatesSelectedAccount() =
        runTest {
            val vm = buildDashboardViewModel()
            val account = UiBankAccount.dummy

            vm.onIntent(DashboardPageIntent.SelectAccount(account))

            kotlin.test.assertEquals(account, vm.state.value.selectedAccount)
        }
}
