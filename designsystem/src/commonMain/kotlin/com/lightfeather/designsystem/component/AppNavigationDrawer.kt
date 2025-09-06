package com.lightfeather.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppAlwaysExpandedNavigationDrawer(
    items: List<AppNavItem>,
    primaryActionContent: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
) {
    Column(
        verticalArrangement = verticalArrangement,
        modifier = Modifier
            .fillMaxHeight()
            .background(colors.navigationDrawerContainerColor)
            .widthIn(max = DrawerDefaults.MaximumDrawerWidth)
    ) {
        items.forEach { item ->
            NavigationDrawerItem(
                icon = item.icon,
                label = { item.label?.invoke() },
                selected = item.selected,
                onClick = item.onClick,
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = item.colors.selectedContainerColor,
                    unselectedContainerColor = item.colors.unselectedContainerColor,
                    selectedIconColor = item.colors.selectedIconColor,
                    unselectedIconColor = item.colors.unselectedIconColor,
                    selectedTextColor = item.colors.selectedTextColor,
                    unselectedTextColor = item.colors.unselectedTextColor,
                    selectedBadgeColor = item.colors.indicatorColor,
                    unselectedBadgeColor = item.colors.unselectedTextColor
                ),
                shape = RectangleShape
            )

        }
        primaryActionContent()

    }
}


@Preview
@Composable
private fun PreviewAppNavigationDrawer() {
    AppTheme {
        AppAlwaysExpandedNavigationDrawer(
            items = List(4) {
                AppNavItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    },
                    label = { Text("Menu") },
                    selected = it == 0,
                    onClick = {},
                    colors = AppNavigationItemColors.defaultColors().copy(
                        selectedIconColor = AppTheme.colors.primary,
                        unselectedIconColor = AppTheme.colors.surfaceLight,
                        selectedTextColor = AppTheme.colors.primary,
                        unselectedTextColor = AppTheme.colors.surfaceLight,
                        selectedContainerColor = AppTheme.colors.surfaceLight,
                        unselectedContainerColor = Color.Transparent,
                    )
                )
            },
            primaryActionContent = {
                // Primary action content (e.g., a logo or header)
            }
        )
    }
}
