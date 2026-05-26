@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.currencies

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.masarify.test.buildCurrenciesViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CurrenciesPageViewModelTest {
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
    fun initialStateHasNoDialogsAndNotEditMode() =
        runTest {
            val vm = buildCurrenciesViewModel()

            assertFalse(vm.state.value.showAddEditDialog)
            assertFalse(vm.state.value.showDeleteDialog)
            assertFalse(vm.state.value.isEditMode)
        }

    @Test
    fun selectBaseCurrencyUpdatesStateCurrency() =
        runTest {
            val vm = buildCurrenciesViewModel()
            val currency = UiCurrency.dummy

            vm.onIntent(CurrenciesPageIntent.SelectBaseCurrency(currency))

            assertEquals(currency, vm.state.value.baseCurrency)
        }

    @Test
    fun toggleEditModeUpdatesIsEditMode() =
        runTest {
            val vm = buildCurrenciesViewModel()

            vm.onIntent(CurrenciesPageIntent.ToggleEditMode(true))

            assertTrue(vm.state.value.isEditMode)
        }

    @Test
    fun showAddCurrencyDialogUpdatesState() =
        runTest {
            val vm = buildCurrenciesViewModel()

            vm.onIntent(CurrenciesPageIntent.ShowAddCurrencyDialog(true))

            assertTrue(vm.state.value.showAddEditDialog)
            assertNull(vm.state.value.editingCurrency)
        }

    @Test
    fun showDeleteDialogUpdatesState() =
        runTest {
            val vm = buildCurrenciesViewModel()
            val currency = UiCurrency.dummy

            vm.onIntent(CurrenciesPageIntent.ShowDeleteDialog(currency))

            assertTrue(vm.state.value.showDeleteDialog)
            assertEquals(currency, vm.state.value.deletingCurrency)
        }

    @Test
    fun hideDialogsHidesAllDialogs() =
        runTest {
            val vm = buildCurrenciesViewModel()
            vm.onIntent(CurrenciesPageIntent.ShowAddCurrencyDialog(true))

            vm.onIntent(CurrenciesPageIntent.HideDialogs)

            assertFalse(vm.state.value.showAddEditDialog)
            assertFalse(vm.state.value.showDeleteDialog)
        }
}
