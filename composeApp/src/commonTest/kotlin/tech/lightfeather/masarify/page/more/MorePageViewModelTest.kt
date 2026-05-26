@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.more

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.buildMoreViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MorePageViewModelTest {
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
    fun toggleDarkThemeTrueUpdatesIsDarkTheme() =
        runTest {
            val vm = buildMoreViewModel()

            vm.onIntent(MorePageIntent.ToggleDarkTheme(enabled = true))

            assertTrue(vm.state.value.isDarkTheme)
        }

    @Test
    fun toggleDarkThemeFalseUpdatesIsDarkTheme() =
        runTest {
            val vm = buildMoreViewModel()
            vm.onIntent(MorePageIntent.ToggleDarkTheme(enabled = true))

            vm.onIntent(MorePageIntent.ToggleDarkTheme(enabled = false))

            assertFalse(vm.state.value.isDarkTheme)
        }

    @Test
    fun showLogoutAllDialogSetsIsLogoutAllDialogVisibleTrue() =
        runTest {
            val vm = buildMoreViewModel()

            vm.onIntent(MorePageIntent.ShowLogoutAllDialog)

            assertTrue(vm.state.value.isLogoutAllDialogVisible)
        }

    @Test
    fun dismissLogoutAllDialogSetsIsLogoutAllDialogVisibleFalse() =
        runTest {
            val vm = buildMoreViewModel()
            vm.onIntent(MorePageIntent.ShowLogoutAllDialog)

            vm.onIntent(MorePageIntent.DismissLogoutAllDialog)

            assertFalse(vm.state.value.isLogoutAllDialogVisible)
        }

    @Test
    fun clearNavigationSetsSelectedDetailItemNull() =
        runTest {
            val vm = buildMoreViewModel()
            vm.onIntent(MorePageIntent.NavigationIntent.SelectCurrencyManagementDetail)

            vm.onIntent(MorePageIntent.ClearNavigation)

            assertNull(vm.state.value.selectedDetailItem)
        }
}
