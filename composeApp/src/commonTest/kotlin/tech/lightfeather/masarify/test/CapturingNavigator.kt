@file:Suppress("EmptyFunctionBlock")

package tech.lightfeather.masarify.test

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import tech.lightfeather.masarify.navigation.ListDetailNavigator
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.PreviewListDetailNavigator
import tech.lightfeather.masarify.navigation.Route

internal class CapturingNavigator : Navigator {
    val navigatedRoutes = mutableListOf<Route>()
    val clearedAndNavigatedRoutes = mutableListOf<Route>()
    var navigateUpCallCount = 0

    override val backStack: NavBackStack<out NavKey>
        get() = throw UnsupportedOperationException("Not supported in test")

    override fun navigate(route: Route) {
        navigatedRoutes.add(route)
    }

    override fun navigateAndClearBackStack(route: Route) {
        clearedAndNavigatedRoutes.add(route)
    }

    override fun navigateUp() {
        navigateUpCallCount++
    }

    override fun popBackStack() {}

    override fun canNavigateBack(): Boolean = false

    @Composable
    override fun forListDetail(initialRoute: NavKey): ListDetailNavigator = PreviewListDetailNavigator
}
