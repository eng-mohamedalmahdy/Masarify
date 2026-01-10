package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Compact pagination controls with minimal space usage
 * Shows simple "← Page X of Y →" format
 *
 * @param currentPage Current page number (1-indexed for display)
 * @param totalPages Total number of pages
 * @param onPreviousPage Callback for previous page button
 * @param onNextPage Callback for next page button
 * @param modifier Modifier for the component
 */
@Composable
fun CompactPaginationControls(
    currentPage: Int,
    totalPages: Int,
    @Suppress("UNUSED_PARAMETER") onPageChange: (Int) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = AppTheme.dimens.elevation.level1,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.dimens.spacing.padding.medium,
                        vertical = AppTheme.dimens.spacing.padding.small,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Previous page button
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 1,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous page",
                    tint =
                        if (currentPage > 1) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        },
                )
            }

            // Page indicator
            Text(
                text = "Page $currentPage of $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Next page button
            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next page",
                    tint =
                        if (currentPage < totalPages) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CompactPaginationControlsPreview() {
    AppTheme {
        CompactPaginationControls(
            currentPage = 5,
            totalPages = 20,
            onPageChange = {},
            onPreviousPage = {},
            onNextPage = {},
        )
    }
}
