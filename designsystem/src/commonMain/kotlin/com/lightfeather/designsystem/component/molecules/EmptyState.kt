package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * EmptyState molecule component for displaying empty state messages
 * Follows atomic design principles and uses design tokens
 *
 * @param title The main title text
 * @param message The descriptive message text
 * @param icon The icon to display above the text
 * @param action Optional action button composable
 * @param modifier Modifier for the root container
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Receipt,
    action: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(AppTheme.dimens.large),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimens.huge),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.normal))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.medium))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = AppTheme.dimens.default),
            )

            action?.let {
                Spacer(modifier = Modifier.height(AppTheme.dimens.large))
                it()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewEmptyState() {
    AppTheme {
        EmptyState(
            title = "No transactions yet",
            message = "Start tracking your finances by adding your first transaction",
        )
    }
}

@Preview
@Composable
private fun PreviewEmptyStateWithAction() {
    AppTheme {
        EmptyState(
            title = "No transactions yet",
            message = "Start tracking your finances by adding your first transaction",
            action = {
                PrimaryButton(
                    onClick = { /* TODO */ },
                ) {
                    Text("Add Transaction")
                }
            },
        )
    }
}
