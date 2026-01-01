package com.lightfeather.masarify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

class NavigatorImpl(
    private val navBackStack: NavBackStack<out NavKey>,
) : Navigator {
    override val backStack: NavBackStack<out NavKey>
        get() = navBackStack

    @Suppress("UNCHECKED_CAST")
    override fun navigate(route: Route) {
        // Add to back stack (launchSingleTop behavior)
        // Check if already at this destination
        if (backStack.isNotEmpty()) {
            val lastRoute = backStack.last()
            // Simple comparison - in production you might want more sophisticated logic
            if (lastRoute.toString().contains(route.routeName)) {
                return
            }
        }

        (navBackStack as NavBackStack<NavKey>).add(route)
    }

    @Suppress("UNCHECKED_CAST")
    override fun navigateAndClearBackStack(route: Route) {
        while (backStack.size > 1) {
            navBackStack.removeLastOrNull()
        }
        if (backStack.isNotEmpty()) {
            navBackStack.removeLastOrNull()
        }
        (navBackStack as NavBackStack<NavKey>).add(route)
    }

    override fun navigateUp() {
        if (backStack.size > 1) {
            navBackStack.removeLastOrNull()
        }
    }

    override fun popBackStack() {
        if (backStack.isNotEmpty()) {
            navBackStack.removeLastOrNull()
        }
    }

    override fun canNavigateBack(): Boolean = backStack.size > 1

    @Composable
    override fun forListDetail(initialRoute: NavKey): ListDetailNavigator {
        // Create a scoped back stack for list-detail navigation
        val listDetailBackStack =
            rememberNavBackStack(
                configuration = NavigationRegistry.savedStateConfiguration,
                initialRoute,
            )

        // Return a list-detail navigator wrapping the scoped back stack
        return remember(initialRoute) {
            ListDetailNavigatorImpl(listDetailBackStack)
        }
    }
}
