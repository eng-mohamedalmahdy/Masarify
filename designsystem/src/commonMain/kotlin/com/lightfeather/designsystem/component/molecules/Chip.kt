package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.AssistChip as MaterialAssistChip
import androidx.compose.material3.FilterChip as MaterialFilterChip

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: SelectableChipColors =
        FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
        ),
) {
    MaterialFilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = colors,
    )
}

@Composable
fun AssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: ChipColors =
        AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
) {
    MaterialAssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = colors,
    )
}

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClose: (() -> Unit)? = null,
) {
    AssistChip(
        onClick = { /* Tags are not clickable */ },
        label = { Text(text) },
        modifier = modifier,
        leadingIcon = null,
        trailingIcon =
            onClose?.let {
                {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textColor,
                        modifier = Modifier.clickable { onClose() },
                    )
                }
            },
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor = color,
                labelColor = textColor,
                trailingIconContentColor = textColor,
            ),
    )
}

@Preview
@Composable
private fun PreviewChips() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var selected by remember { mutableStateOf(false) }

            // Filter chip
            FilterChip(
                selected = selected,
                onClick = { selected = !selected },
                label = { Text("Filter") },
            )

            // Assist chip
            AssistChip(
                onClick = { /* Handle click */ },
                label = { Text("Assist") },
                leadingIcon = { Text("🔍") },
            )

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Tag(text = "Tag 1")
                Tag(
                    text = "Tag 2",
                    onClose = { /* Handle close */ },
                )
                Tag(
                    text = "Custom",
                    color = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}
