package com.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- DATA / ITEM SCOPE ----------------------------------------------------

/**
 * A single navigation item representation.
 * `icon` / `label` are stored as composable lambdas and invoked later by container.
 */
data class AppNavItem(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val label: (@Composable () -> Unit)? = null,
    val alwaysShowLabel: Boolean? = null,
    val enabled: Boolean = true,
    val badge: (@Composable () -> Unit)? = null,
    val colors: AppNavigationItemColors,
)

data class AppNavigationItemColors(
    val selectedIconColor: Color,
    val unselectedIconColor: Color,
    val selectedTextColor: Color,
    val unselectedTextColor: Color,
    val selectedContainerColor: Color,
    val unselectedContainerColor: Color,
    val indicatorColor: Color,
) {
    companion object {
        @Composable
        fun defaultColors(): AppNavigationItemColors {
            val defaults: NavigationBarItemColors = NavigationBarItemDefaults.colors()
            return AppNavigationItemColors(
                selectedIconColor = defaults.selectedIconColor,
                unselectedIconColor = defaults.unselectedIconColor,
                selectedTextColor = defaults.selectedTextColor,
                unselectedTextColor = defaults.unselectedTextColor,
                indicatorColor = defaults.selectedIndicatorColor,
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        }
    }
}

/**
 * Mutable builder used by the user to declare navigation items.
 * The composable `content` will be invoked with this scope to fill `items`.
 */
class AppNavigationItemsScope internal constructor() {
    internal val items = mutableStateListOf<AppNavItem>()

    internal fun clear() = items.clear()

    /**
     * Add a nav item. This must be a regular function (not @Composable).
     * We store composable lambdas for icon/label/badge — they will be invoked later
     * when rendering in the container.
     */

    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        label: (@Composable () -> Unit)? = null,
        alwaysShowLabel: Boolean? = null,
        enabled: Boolean = true,
        badge: (@Composable () -> Unit)? = null,
        colors: AppNavigationItemColors,
    ) {
        items +=
            AppNavItem(
                selected = selected,
                onClick = onClick,
                icon = icon,
                label = label,
                alwaysShowLabel = alwaysShowLabel,
                enabled = enabled,
                badge = badge,
                colors = colors,
            )
    }
}

// --- CONTAINER DSL SCOPE --------------------------------------------------

/**
 * Container builder map: register a custom container for any NavigationSuiteType.
 * The container receives:
 *  - compiled list of AppNavItem
 *  - primaryActionContent (e.g. FAB)
 *  - verticalArrangement for rails/drawers
 *  - colors so custom container can honour theme
 */
class AppNavigationSuiteScope {
    private val map =
        mutableMapOf<
            NavigationSuiteType,
            @Composable (
                items: List<AppNavItem>,
                primaryActionContent: @Composable () -> Unit,
                verticalArrangement: Arrangement.Vertical,
                colors: NavigationSuiteColors,
            ) -> Unit,
        >()

    fun set(
        type: NavigationSuiteType,
        container: @Composable (
            items: List<AppNavItem>,
            primaryActionContent: @Composable () -> Unit,
            verticalArrangement: Arrangement.Vertical,
            colors: NavigationSuiteColors,
        ) -> Unit,
    ) {
        map[type] = container
    }

    internal fun resolve(
        type: NavigationSuiteType,
    ): (
        @Composable (
            items: List<AppNavItem>,
            primaryActionContent: @Composable () -> Unit,
            verticalArrangement: Arrangement.Vertical,
            colors: NavigationSuiteColors,
        ) -> Unit
    )? =
        map[type]
}

// --- APP NAVIGATION SUITE -------------------------------------------------

@Composable
fun AppNavigationSuite(
    navigationSuiteType: NavigationSuiteType,
    modifier: Modifier = Modifier,
    navigationSuiteColors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    navigationItemVerticalArrangement: Arrangement.Vertical = NavigationSuiteDefaults.verticalArrangement,
    primaryActionContent: @Composable (() -> Unit) = {},
    // Items DSL (declare items once)
    content: @Composable AppNavigationItemsScope.() -> Unit,
    // Container overrides
    builder: AppNavigationSuiteScope.() -> Unit = {},
) {
    // Build container overrides
    val suiteScope = remember { AppNavigationSuiteScope() }.apply(builder)

    // Items scope: remember so the list survives recompositions
    val itemsScope = remember { AppNavigationItemsScope() }

    // Clear previous entries and populate afresh by invoking the composable DSL.
    // (content is a @Composable AppNavigationItemsScope.() -> Unit)
    itemsScope.clear()
    content(itemsScope)

    // IMPORTANT: don't wrap in `remember` keyed by the mutableStateList reference.
    // Just snapshot contents so Render helpers see current state.
    val items: List<AppNavItem> by remember(itemsScope.items) { derivedStateOf { itemsScope.items.toList() } }

    LaunchedEffect(Unit) { /* no-op to satisfy lint; items are reset every composition below */ }
    itemsScope.clear()
    content(itemsScope) // this is a @Composable invocation that will run in the composition and *only* add items

    // Resolve custom container if provided
    val customContainer = suiteScope.resolve(navigationSuiteType)

    if (customContainer != null) {
        // Let the custom container render items (it will call item.icon(), item.label(), etc.)
        customContainer(items, primaryActionContent, navigationItemVerticalArrangement, navigationSuiteColors)
        return
    }

    // --- Fallback to Material3-ish containers (replicates NavigationSuite behavior) ---
    // We choose explicit containers so the items DSL is universal but the fallback uses Material components.
    when (navigationSuiteType) {
        NavigationSuiteType.ShortNavigationBarCompact,
        NavigationSuiteType.ShortNavigationBarMedium,
        -> {
            ShortNavigationBar(
                modifier = modifier,
                containerColor = navigationSuiteColors.shortNavigationBarContainerColor,
                contentColor = navigationSuiteColors.shortNavigationBarContentColor,
            ) {
                RenderAsNavigationBarItems(items)
            }
        }

        NavigationSuiteType.NavigationBar -> {
            // Visual equivalent of NavigationBar: taller short bar
            ShortNavigationBar(
                modifier = modifier,
                containerColor = navigationSuiteColors.shortNavigationBarContainerColor,
                contentColor = navigationSuiteColors.shortNavigationBarContentColor,
            ) {
                RenderAsNavigationBarItems(items)
            }
        }

        NavigationSuiteType.WideNavigationRailCollapsed -> {
            WideNavigationRail(
                modifier = modifier,
                header = primaryActionContent,
                arrangement = navigationItemVerticalArrangement,
                colors = navigationSuiteColors.wideNavigationRailColors,
            ) {
                RenderAsRailItems(items)
            }
        }

        NavigationSuiteType.WideNavigationRailExpanded -> {
            WideNavigationRail(
                modifier = modifier,
                header = primaryActionContent,
                state = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Expanded),
                arrangement = navigationItemVerticalArrangement,
                colors = navigationSuiteColors.wideNavigationRailColors,
            ) {
                RenderAsRailItems(items)
            }
        }

        NavigationSuiteType.NavigationRail -> {
            NavigationRail(
                modifier = modifier,
                header = { primaryActionContent() },
                containerColor = navigationSuiteColors.navigationRailContainerColor,
                contentColor = navigationSuiteColors.navigationRailContentColor,
            ) {
                if (
                    navigationItemVerticalArrangement == Arrangement.Center ||
                    navigationItemVerticalArrangement == Arrangement.Bottom
                ) {
                    Spacer(Modifier.weight(1f))
                }
                RenderAsRailItems(items)
                if (navigationItemVerticalArrangement == Arrangement.Center) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        NavigationSuiteType.NavigationDrawer -> {
            PermanentDrawerSheet(
                modifier = modifier,
                drawerContainerColor = navigationSuiteColors.navigationDrawerContainerColor,
                drawerContentColor = navigationSuiteColors.navigationDrawerContentColor,
            ) {
                primaryActionContent()
                if (
                    navigationItemVerticalArrangement == Arrangement.Center ||
                    navigationItemVerticalArrangement == Arrangement.Bottom
                ) {
                    Spacer(Modifier.weight(1f))
                }
                RenderAsDrawerItems(items)
                if (navigationItemVerticalArrangement == Arrangement.Center) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// --- RENDER HELPERS -------------------------------------------------------

@Composable
private fun RenderAsNavigationBarItems(items: List<AppNavItem>) {
    items.forEach {
        ShortNavigationBarItem(
            selected = it.selected,
            onClick = it.onClick,
            icon = { it.icon() },
            label = it.label,
            enabled = it.enabled,
            colors =
                ShortNavigationBarItemDefaults.colors(
                    selectedIconColor = it.colors.selectedIconColor,
                    unselectedIconColor = it.colors.unselectedIconColor,
                    selectedTextColor = it.colors.selectedTextColor,
                    unselectedTextColor = it.colors.unselectedTextColor,
                    selectedIndicatorColor = it.colors.indicatorColor,
                ),
        )
    }
}

@Composable
private fun RenderAsRailItems(items: List<AppNavItem>) {
    // Material's NavigationRailItem expects a slightly different API but we can map
    items.forEach { item ->
        NavigationRailItem(
            selected = item.selected,
            onClick = item.onClick,
            icon = { item.icon() },
            label = item.label,
            alwaysShowLabel = item.alwaysShowLabel ?: false,
            enabled = item.enabled,
            colors =
                NavigationRailItemDefaults.colors(
                    selectedIconColor = item.colors.selectedIconColor,
                    unselectedIconColor = item.colors.unselectedIconColor,
                    selectedTextColor = item.colors.selectedTextColor,
                    unselectedTextColor = item.colors.unselectedTextColor,
                    indicatorColor = item.colors.indicatorColor,
                ),
        )
    }
}

@Composable
private fun RenderAsDrawerItems(items: List<AppNavItem>) {
    // In Material3 there's NavigationDrawerItem — map to it.
    items.forEach { item ->
        // If you use your own drawer-row composable, switch it here.
        NavigationDrawerItem(
            label = { item.label?.invoke() ?: Spacer(Modifier) },
            selected = item.selected,
            onClick = item.onClick,
            icon = { item.icon() },
            colors =
                NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = item.colors.selectedContainerColor,
                    unselectedContainerColor = item.colors.unselectedContainerColor,
                    selectedTextColor = item.colors.selectedTextColor,
                    unselectedTextColor = item.colors.unselectedTextColor,
                    selectedIconColor = item.colors.selectedIconColor,
                    unselectedIconColor = item.colors.unselectedIconColor,
                ),
            // note: NavigationDrawerItem has many params; adapt as you need
        )
    }
}

@Preview
@Composable
private fun PreviewAppNavigationSuite() {
    AppTheme {
        AppNavigationSuite(
            navigationSuiteType = NavigationSuiteType.NavigationBar,
            modifier = Modifier.fillMaxSize(),
            builder = {},
            content = {},
        )
    }
}
