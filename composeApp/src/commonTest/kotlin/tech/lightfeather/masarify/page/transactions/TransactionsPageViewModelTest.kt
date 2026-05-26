@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.transactions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiTransactionFilter
import tech.lightfeather.masarify.test.buildTransactionsViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionsPageViewModelTest {
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
            val vm = buildTransactionsViewModel()
            val currency = UiCurrency.dummy

            vm.onIntent(TransactionsPageIntent.SelectCurrency(currency))

            assertEquals(currency, vm.state.value.selectedCurrency)
        }

    @Test
    fun showFilterDialogSetsShowFilterDialogTrue() =
        runTest {
            val vm = buildTransactionsViewModel()

            vm.onIntent(TransactionsPageIntent.ShowFilterDialog)

            assertTrue(vm.state.value.showFilterDialog)
        }

    @Test
    fun hideFilterDialogSetsShowFilterDialogFalse() =
        runTest {
            val vm = buildTransactionsViewModel()
            vm.onIntent(TransactionsPageIntent.ShowFilterDialog)

            vm.onIntent(TransactionsPageIntent.HideFilterDialog)

            assertFalse(vm.state.value.showFilterDialog)
        }

    @Test
    fun updateFilterUpdatesFilterState() =
        runTest {
            val vm = buildTransactionsViewModel()
            val filter = UiTransactionFilter.EMPTY

            vm.onIntent(TransactionsPageIntent.UpdateFilter(filter))

            assertEquals(filter, vm.state.value.filter)
            assertEquals(0, vm.state.value.currentPage)
        }

    @Test
    fun clearTransactionContextClearsEditingState() =
        runTest {
            val vm = buildTransactionsViewModel()

            vm.onIntent(TransactionsPageIntent.ClearTransactionContext)

            assertNull(vm.state.value.editingTransaction)
            assertNull(vm.state.value.lockedFromAccount)
            assertNull(vm.state.value.initialCategory)
        }
}
