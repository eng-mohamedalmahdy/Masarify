@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.categories.addedit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.buildAddEditCategoryViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddEditCategoryPageViewModelTest {
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
            val vm = buildAddEditCategoryViewModel()

            vm.onIntent(AddEditCategoryPageIntent.UpdateName("Food"))

            assertEquals("Food", vm.state.value.name)
        }

    @Test
    fun updateDescriptionIntentUpdatesStateDescription() =
        runTest {
            val vm = buildAddEditCategoryViewModel()

            vm.onIntent(AddEditCategoryPageIntent.UpdateDescription("Groceries"))

            assertEquals("Groceries", vm.state.value.description)
        }

    @Test
    fun updateCustomIconUrlIntentUpdatesState() =
        runTest {
            val vm = buildAddEditCategoryViewModel()

            vm.onIntent(AddEditCategoryPageIntent.UpdateCustomIconUrl("https://example.com/icon.png"))

            assertEquals("https://example.com/icon.png", vm.state.value.customIconUrl)
        }

    @Test
    fun selectColorIntentUpdatesSelectedColor() =
        runTest {
            val vm = buildAddEditCategoryViewModel()

            vm.onIntent(AddEditCategoryPageIntent.SelectColor("#FF0000"))

            assertEquals("#FF0000", vm.state.value.selectedColor)
        }

    @Test
    fun toggleColorPickerIntentUpdatesShowColorPicker() =
        runTest {
            val vm = buildAddEditCategoryViewModel()

            vm.onIntent(AddEditCategoryPageIntent.ToggleColorPicker(true))

            assertTrue(vm.state.value.showColorPicker)
        }

    @Test
    fun cancelIntentCallsOnBackCallback() =
        runTest {
            var cancelCalled = false
            val vm = buildAddEditCategoryViewModel(onBackCallback = { cancelCalled = true })

            vm.onIntent(AddEditCategoryPageIntent.Cancel)

            assertTrue(cancelCalled)
        }
}
