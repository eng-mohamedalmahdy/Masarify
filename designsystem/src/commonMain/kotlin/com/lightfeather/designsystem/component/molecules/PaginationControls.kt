package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.model.PageSize
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Pagination controls for navigating through paged data
 * Supports page navigation and configurable page size
 *
 * @param currentPage Current page number (0-indexed)
 * @param totalPages Total number of pages
 * @param pageSize Current page size
 * @param totalItems Total number of items across all pages
 * @param onPageChange Callback when page changes
 * @param onPageSizeChange Callback when page size changes
 * @param modifier Modifier for the component
 */
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    pageSize: PageSize,
    totalItems: Long,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (PageSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPageSizeMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(AppTheme.dimens.spacing.padding.medium),
        horizontalArrangement =
            Arrangement.spacedBy(
                AppTheme.dimens.spacing.padding.medium,
                Alignment.CenterHorizontally,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Previous page button
        IconButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 0,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous Page",
                tint =
                    if (currentPage > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
            )
        }

        // Page info
        Text(
            text = "Page ${currentPage + 1} of ${totalPages.coerceAtLeast(1)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Next page button
        IconButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages - 1,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Page",
                tint =
                    if (currentPage < totalPages - 1) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
            )
        }

        // Divider
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )

        // Total items count
        Text(
            text = "$totalItems items",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Page size selector
        TextButton(
            onClick = { showPageSizeMenu = true },
        ) {
            Text(
                text = "${pageSize.value} per page",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Page size dropdown menu
        DropdownMenu(
            expanded = showPageSizeMenu,
            onDismissRequest = { showPageSizeMenu = false },
        ) {
            PageSize.entries.forEach { size ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${size.value} per page",
                            color =
                                if (size == pageSize) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    },
                    onClick = {
                        onPageSizeChange(size)
                        showPageSizeMenu = false
                    },
                )
            }
        }
    }
}

/**
 * Advanced pagination controls with page number selector
 * Shows individual page numbers with ellipsis for better navigation
 *
 * @param currentPage Current page number (1-indexed for display, will be converted to 0-indexed)
 * @param totalPages Total number of pages
 * @param onPageChange Callback when page changes (receives 1-indexed page number)
 * @param onPreviousPage Callback for previous page button
 * @param onNextPage Callback for next page button
 * @param modifier Modifier for the component
 */
@Composable
fun AdvancedPaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(AppTheme.dimens.default),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.elevation.level1),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.dimens.default,
                        vertical = AppTheme.dimens.compact,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 1,
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous page",
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val visiblePages = getVisiblePages(currentPage, totalPages)

                items(visiblePages) { page ->
                    when (page) {
                        -1 ->
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )

                        else ->
                            PageNumber(
                                page = page,
                                isSelected = page == currentPage,
                                onClick = { onPageChange(page) },
                            )
                    }
                }
            }

            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages,
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next page",
                )
            }
        }
    }
}

@Composable
private fun PageNumber(
    page: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(AppTheme.dimens.touchTarget.min)
                .clip(CircleShape)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Card(
                modifier = Modifier.size(AppTheme.dimens.large),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                shape = CircleShape,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = page.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        } else {
            Text(
                text = page.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Calculate visible page numbers with ellipsis
 * Returns a list where -1 represents an ellipsis
 */
private fun getVisiblePages(
    currentPage: Int,
    totalPages: Int,
): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val pages = mutableListOf<Int>()

    pages.add(1)

    when {
        currentPage <= 4 -> {
            pages.addAll(2..5)
            pages.add(-1)
            pages.add(totalPages)
        }

        currentPage >= totalPages - 3 -> {
            pages.add(-1)
            pages.addAll((totalPages - 4)..(totalPages - 1))
            pages.add(totalPages)
        }

        else -> {
            pages.add(-1)
            pages.addAll((currentPage - 1)..(currentPage + 1))
            pages.add(-1)
            pages.add(totalPages)
        }
    }

    return pages
}

@Preview
@Composable
private fun PaginationControlsPreview() {
    AppTheme {
        PaginationControls(
            currentPage = 2,
            totalPages = 10,
            pageSize = PageSize.MEDIUM,
            totalItems = 195,
            onPageChange = {},
            onPageSizeChange = {},
        )
    }
}

@Preview
@Composable
private fun AdvancedPaginationControlsPreview() {
    AppTheme {
        AdvancedPaginationControls(
            currentPage = 5,
            totalPages = 20,
            onPageChange = {},
            onPreviousPage = {},
            onNextPage = {},
        )
    }
}
