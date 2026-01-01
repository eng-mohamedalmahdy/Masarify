package com.lightfeather.masarify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface Navigator {
    val backStack: NavBackStack<out NavKey>

    fun navigate(route: Route)

    fun navigateAndClearBackStack(route: Route)

    fun navigateUp()

    fun popBackStack()

    fun canNavigateBack(): Boolean

    /**
     * Creates a scoped [ListDetailNavigator] for adaptive list-detail navigation patterns.
     *
     * This method provides a centralized way to create list-detail navigators with
     * proper serialization configuration and back stack management.
     *
     * @param initialRoute The initial route (typically the list route) for this list-detail context
     * @return A [ListDetailNavigator] instance for managing list-detail navigation
     */
    @Composable
    fun forListDetail(initialRoute: NavKey): ListDetailNavigator
}

/**
 * Preview-only Navigator implementation for Compose Preview functions.
 * All methods are no-ops since navigation doesn't work in previews.
 */
@Suppress("EmptyFunctionBlock")
object PreviewNavigator : Navigator {
    override val backStack: NavBackStack<out NavKey>
        get() = throw UnsupportedOperationException("Navigator not available in Preview")

    override fun navigate(route: Route) {}
    override fun navigateAndClearBackStack(route: Route) {}
    override fun navigateUp() {}
    override fun popBackStack() {}
    override fun canNavigateBack(): Boolean = false

    @Composable
    override fun forListDetail(initialRoute: NavKey): ListDetailNavigator = PreviewListDetailNavigator
}

/**
 * Preview-only ListDetailNavigator implementation for Compose Preview functions.
 */
@Suppress("EmptyFunctionBlock")
object PreviewListDetailNavigator : ListDetailNavigator {
    override val backStack: NavBackStack<out NavKey>
        get() = throw UnsupportedOperationException("ListDetailNavigator not available in Preview")

    override fun navigateToDetail(key: NavKey) {}
    override fun navigateToList() {}
    override fun canNavigateBack(): Boolean = false
    override fun back() {}
}
