package com.lightfeather.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.model.UiBottomNavigationItem
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppBottomNavigation(
    navItems: List<UiBottomNavigationItem>,
    modifier: Modifier = Modifier,
    isCurrentDestination: (UiBottomNavigationItem) -> Boolean,
    onItemClick: (UiBottomNavigationItem) -> Unit,
) {
    NavigationBar(
        modifier =
            modifier.shadow(
                elevation = 20.dp,
                shape = RectangleShape,
                clip = true,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        navItems.forEach { navigationItem ->
            val selected = isCurrentDestination(navigationItem)
            NavigationBarItem(
                icon = {
                    AppImage(
                        navigationItem.icon,
                        contentDescription = stringResource(navigationItem.label),
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.large)
                                .padding(bottom = AppTheme.dimens.small),
                        colorFilter =
                            ColorFilter.tint(
                                if (selected.not()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                            ),
                    )
                },
                label = {
                    Text(
                        stringResource(navigationItem.label),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected.not()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                    )
                },
                selected = selected,
                onClick = { onItemClick(navigationItem) },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAppBottomNavigation() {
    AppTheme {
        AppBottomNavigation(
            navItems = listOf(),
            isCurrentDestination = { true },
            onItemClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
