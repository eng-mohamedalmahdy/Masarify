@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.bankaccounts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.masarify.test.buildBankAccountsViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class BankAccountsPageViewModelTest {
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
    fun selectCurrencyUpdatesSelectedCurrency() =
        runTest {
            val vm = buildBankAccountsViewModel()
            val currency = UiCurrency.dummy

            vm.onIntent(BankAccountsPageIntent.SelectCurrency(currency))

            assertEquals(currency, vm.state.value.selectedCurrency)
        }

    @Test
    fun selectAccountUpdatesSelectedAccount() =
        runTest {
            val vm = buildBankAccountsViewModel()
            val account = UiBankAccount.dummy

            vm.onIntent(BankAccountsPageIntent.SelectAccount(account))

            assertEquals(account, vm.state.value.selectedAccount)
        }

    @Test
    fun cancelDeleteTransactionHidesDialog() =
        runTest {
            val vm = buildBankAccountsViewModel()

            vm.onIntent(BankAccountsPageIntent.CancelDeleteTransaction)

            assertFalse(vm.state.value.showAddEditDialog)
            assertNull(vm.state.value.underProcessTransaction)
        }

    @Test
    fun cancelUpdateTransactionHidesDialog() =
        runTest {
            val vm = buildBankAccountsViewModel()

            vm.onIntent(BankAccountsPageIntent.CancelUpdateTransaction)

            assertFalse(vm.state.value.showAddEditDialog)
            assertNull(vm.state.value.underProcessTransaction)
        }
}
