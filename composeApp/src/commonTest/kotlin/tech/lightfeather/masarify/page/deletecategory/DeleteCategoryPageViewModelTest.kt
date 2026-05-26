@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.deletecategory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildDeleteCategoryViewModel
import tech.lightfeather.masarify.test.testCategory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteCategoryPageViewModelTest {
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
    fun categoryPropertyMatchesInput() =
        runTest {
            val vm = buildDeleteCategoryViewModel(category = testCategory)

            assertEquals(testCategory.name, vm.category.name)
        }

    @Test
    fun onCancelCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildDeleteCategoryViewModel(navigator = navigator)

            vm.onCancel()

            assertTrue(navigator.navigateUpCallCount == 1)
        }

    @Test
    fun onConfirmCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildDeleteCategoryViewModel(navigator = navigator)

            vm.onConfirm()

            assertTrue(navigator.navigateUpCallCount == 1)
        }
}
