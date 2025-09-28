package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MoreListItem(
    text: String,
    image: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(
                    horizontal = AppTheme.dimens.spacing.padding.medium,
                    vertical = AppTheme.dimens.spacing.padding.small,
                ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Image/Icon
        AppImage(
            model = image,
            contentDescription = contentDescription,
            modifier = Modifier.size(AppTheme.dimens.icon.size.large),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        )

        // Text
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        // Trailing content (switch, dropdown, chevron, etc.)
        trailingContent?.invoke() ?: Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Convenience function for MoreListItem with Switch
@Composable
fun MoreListItemWithSwitch(
    text: String,
    image: Any?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    MoreListItem(
        text = text,
        image = image,
        modifier = modifier,
        contentDescription = contentDescription,
        onClick =
            if (enabled) {
                { onCheckedChange(!checked) }
            } else {
                null
            },
        textColor = textColor,
        trailingContent = {
            com.lightfeather.designsystem.component.atoms.Switch(
                checked = checked,
                onCheckedChange = null, // Handled by row click
                enabled = enabled,
            )
        },
    )
}

// Convenience function for MoreListItem with Dropdown
@Composable
fun <T> MoreListItemWithDropdown(
    text: String,
    image: Any?,
    selectedValue: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    contentRow: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    MoreListItem(
        text = text,
        image = image,
        modifier = modifier,
        contentDescription = contentDescription,
        onClick = null, // Click handled by dropdown
        textColor = textColor,
        trailingContent = {
            AppDropMenu(
                label = "",
                displayedValue = selectedValue,
                items = items,
                onSelectedItem = onItemSelected,
                contentRow = contentRow,
                modifier = Modifier.padding(start = AppTheme.dimens.spacing.padding.small),
            )
        },
    )
}

@Preview
@Composable
private fun PreviewMoreListItem() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MoreListItem(
                text = "Settings",
                image = Icons.Default.ChevronRight,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMoreListItemWithSwitch() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MoreListItemWithSwitch(
                text = "Dark Mode",
                image = Icons.Default.ChevronRight,
                checked = true,
                onCheckedChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMoreListItemWithDropdown() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MoreListItemWithDropdown(
                text = "Language",
                image = Icons.Default.ChevronRight,
                selectedValue = "English",
                items = listOf("English", "Arabic", "French"),
                onItemSelected = {},
                contentRow = { item -> Text(text = item) },
            )
        }
    }
}
