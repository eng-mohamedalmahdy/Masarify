package com.lightfeather.masarify.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Navigator interface for adaptive list-detail navigation patterns.
 *
 * This interface provides a centralized abstraction for managing navigation
 * in list-detail screens, handling phone (stacked) and tablet (side-by-side) layouts
 * automatically through Material3's adaptive layout system.
 *
 * Usage:
 * ```
 * val listDetailNav = navigator.forListDetail(BankAccountsList)
 * listDetailNav.navigateToDetail(ViewBankAccount(accountId))
 * ```
 */
interface ListDetailNavigator {
    /**
     * The underlying navigation back stack.
     * Internal use - prefer using navigation methods instead of manipulating directly.
     */
    val backStack: NavBackStack<out NavKey>

    /**
     * Navigate to a detail pane destination.
     *
     * @param key The navigation key representing the detail destination
     */
    fun navigateToDetail(key: NavKey)

    /**
     * Navigate back to the list pane, clearing all detail destinations.
     */
    fun navigateToList()

    /**
     * Check if back navigation is possible (i.e., there are detail destinations to pop).
     *
     * @return true if back navigation is possible, false if already at list root
     */
    fun canNavigateBack(): Boolean

    /**
     * Navigate back one step in the detail navigation stack.
     * No-op if already at the list root.
     */
    fun back()
}
