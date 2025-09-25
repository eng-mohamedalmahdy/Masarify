package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun TransactionsPane(
    filter: com.lightfeather.designsystem.model.UiTransactionFilter,
    viewModel: TransactionsPanePageViewModel,
) {
    // Update the filter when it changes
    LaunchedEffect(filter) {
        viewModel.updateFilter(filter)
    }

    val transactions by viewModel.transactions.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isEmpty by viewModel.isEmpty.collectAsState()
    val isFiltered by viewModel.isFiltered.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            isLoading && transactions.isEmpty() -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            isEmpty && !isLoading -> {
                EmptyState(
                    title =
                        stringResource(
                            if (isFiltered) {
                                MR.strings.empty_filtered_transactions_title
                            } else {
                                MR.strings.empty_transactions_title
                            },
                        ),
                    message =
                        stringResource(
                            if (isFiltered) {
                                MR.strings.empty_filtered_transactions_message
                            } else {
                                MR.strings.empty_transactions_message
                            },
                        ),
                    modifier = Modifier.weight(1f),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
                ) {
                    items(
                        items = transactions,
                        key = { it.id },
                    ) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { viewModel.onTransactionClick(transaction.id) },
                        )
                    }
                }
            }
        }

        if (totalPages > 1) {
            PaginationFooter(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageChange = { page -> viewModel.goToPage(page) },
                onPreviousPage = { viewModel.previousPage() },
                onNextPage = { viewModel.nextPage() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PaginationFooter(
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
