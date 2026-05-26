@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.categories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.buildCategoriesViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CategoriesPageViewModelTest {
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
    fun loadDataIntentIsHandledWithoutCrash() =
        runTest {
            val vm = buildCategoriesViewModel()

            vm.onIntent(CategoriesPageIntent.LoadData)

            // no-op intent — just verifies it doesn't throw
        }

    @Test
    fun deleteCategoryIntentNavigatesToDeleteCategoryRoute() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildCategoriesViewModel(navigator = navigator)
            val uiCategory =
                UiCategory(
                    id = "1",
                    name = "Food",
                    description = "Food expenses",
                    color = "#FF5722",
                    image = "https://example.com/food.png",
                )

            vm.onIntent(CategoriesPageIntent.DeleteCategory(uiCategory))

            assertTrue(navigator.navigatedRoutes.filterIsInstance<DeleteCategoryRoute>().isNotEmpty())
        }
}
