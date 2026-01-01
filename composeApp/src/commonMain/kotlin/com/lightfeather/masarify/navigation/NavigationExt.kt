package com.lightfeather.masarify.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

/**
 * CompositionLocal for providing [Navigator] to child composables.
 *
 * Usage:
 * ```
 * CompositionLocalProvider(LocalNavigator provides navigator) {
 *     // Child composables can access navigator via LocalNavigator.current
 * }
 * ```
 */
val LocalNavigator =
    staticCompositionLocalOf<Navigator> {
        error("No Navigator provided. Ensure Navigator is provided via CompositionLocalProvider.")
    }

/**
 * Generic display function for main navigation.
 *
 * This extension function wraps Navigation 3's NavDisplay with a generic API
 * that hides library-specific implementation details.
 *
 * @param modifier Modifier to apply to the navigation display
 * @param onBack Callback for back navigation (defaults to popBackStack)
 * @param content Entry provider scope for defining navigation entries
 */
@Composable
fun Navigator.Display(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { popBackStack() },
    content: EntryProviderScope<NavKey>.() -> Unit,
) {
    @Suppress("UNCHECKED_CAST")
    NavDisplay(
        backStack = backStack as androidx.navigation3.runtime.NavBackStack<NavKey>,
        onBack = onBack,
        modifier = modifier,
        entryProvider = entryProvider(builder = content),
    )
}

/**
 * Display function for adaptive list-detail navigation patterns.
 *
 * This extension function wraps Navigation 3's NavDisplay with adaptive layout support,
 * automatically handling phone (stacked) and tablet (side-by-side) layouts.
 *
 * @param modifier Modifier to apply to the navigation display
 * @param onBack Callback for back navigation (defaults to navigator's back)
 * @param sceneStrategy Strategy for list-detail adaptive layout (defaults to Material3's strategy)
 * @param content Entry provider scope for defining list and detail entries
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailNavigator.Display(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { back() },
    sceneStrategy: ListDetailSceneStrategy<NavKey> = rememberListDetailSceneStrategy(),
    content: EntryProviderScope<NavKey>.() -> Unit,
) {
    @Suppress("UNCHECKED_CAST")
    NavDisplay(
        backStack = backStack as androidx.navigation3.runtime.NavBackStack<NavKey>,
        onBack = onBack,
        sceneStrategy = sceneStrategy,
        modifier = modifier,
        entryProvider = entryProvider(builder = content),
    )
}
