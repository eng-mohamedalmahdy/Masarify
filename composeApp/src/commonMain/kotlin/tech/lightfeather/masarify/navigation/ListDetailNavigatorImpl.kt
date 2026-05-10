package tech.lightfeather.masarify.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Implementation of [ListDetailNavigator] for adaptive list-detail navigation patterns.
 *
 * This implementation manages a scoped navigation back stack for list-detail screens,
 * providing methods to navigate between list and detail panes in an adaptive layout.
 *
 * @param navBackStack The underlying Navigation 3 back stack for this list-detail context
 */
internal class ListDetailNavigatorImpl(
    private val navBackStack: NavBackStack<out NavKey>,
) : ListDetailNavigator {
    override val backStack: NavBackStack<out NavKey>
        get() = navBackStack

    @Suppress("UNCHECKED_CAST")
    override fun navigateToDetail(key: NavKey) {
        // Add the detail destination to the back stack
        (navBackStack as NavBackStack<NavKey>).add(key)
    }

    override fun navigateToList() {
        // Clear all detail destinations, returning to list root
        while (canNavigateBack()) {
            back()
        }
    }

    override fun canNavigateBack(): Boolean {
        // Can navigate back if there's more than just the list root in the stack
        return navBackStack.size > 1
    }

    override fun back() {
        if (canNavigateBack()) {
            navBackStack.removeLastOrNull()
        }
    }
}
