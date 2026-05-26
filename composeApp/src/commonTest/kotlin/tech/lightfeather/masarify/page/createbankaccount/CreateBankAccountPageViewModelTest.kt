@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.createbankaccount

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildCreateBankAccountViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CreateBankAccountPageViewModelTest {
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
            val vm = buildCreateBankAccountViewModel()

            vm.onIntent(CreateBankAccountPageIntent.UpdateName("Test Bank"))

            assertEquals("Test Bank", vm.state.value.name)
        }

    @Test
    fun updateDescriptionIntentUpdatesStateDescription() =
        runTest {
            val vm = buildCreateBankAccountViewModel()

            vm.onIntent(CreateBankAccountPageIntent.UpdateDescription("My savings account"))

            assertEquals("My savings account", vm.state.value.description)
        }

    @Test
    fun updateInitialBalanceIntentUpdatesStateBalance() =
        runTest {
            val vm = buildCreateBankAccountViewModel()

            vm.onIntent(CreateBankAccountPageIntent.UpdateInitialBalance("500"))

            assertEquals("500", vm.state.value.initialBalance)
        }

    @Test
    fun updateCurrencyIntentUpdatesStateCurrency() =
        runTest {
            val vm = buildCreateBankAccountViewModel()
            val currency = UiCurrency.dummy

            vm.onIntent(CreateBankAccountPageIntent.UpdateCurrency(currency))

            assertEquals(currency, vm.state.value.currency)
        }

    @Test
    fun toggleDefaultIntentTogglesIsDefault() =
        runTest {
            val vm = buildCreateBankAccountViewModel()
            val initialDefault = vm.state.value.isDefault

            vm.onIntent(CreateBankAccountPageIntent.ToggleDefault)

            assertNotEquals(initialDefault, vm.state.value.isDefault)
        }

    @Test
    fun navigateBackIntentCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildCreateBankAccountViewModel(navigator = navigator)

            vm.onIntent(CreateBankAccountPageIntent.NavigateBack)

            assertTrue(navigator.navigateUpCallCount == 1)
        }
}
