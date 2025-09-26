package com.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountsHeader(
    totalAmountInSelectedOrDefaultCurrency: String,
    defaultCurrency: UiCurrency?,
    selectedCurrency: UiCurrency?,
    totalAccounts: Int,
    userAccountsCurrencies: List<UiCurrency>,
    onCurrencyClick: (UiCurrency?) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.medium,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.component.card,
            ),
        shape = shape,
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.dimens.spacing.padding.small),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.margin.small),
        ) {
            // Header Section
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
            ) {
                Row {
                    Text(
                        text = stringResource(MR.strings.total_wealth),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(MR.strings.accounts_count, totalAccounts),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Total Amount with Currency
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = totalAmountInSelectedOrDefaultCurrency,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = (selectedCurrency ?: defaultCurrency)?.symbol.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
            ) {
                // Original Values Chip (always first)
                item {
                    FilterChip(
                        onClick = { onCurrencyClick(null) },
                        label = {
                            Text(
                                text = stringResource(MR.strings.original_values),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        selected = selectedCurrency == null,
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                }

                // Currency Chips
                items(userAccountsCurrencies.distinct()) { currency ->
                    FilterChip(
                        onClick = { onCurrencyClick(currency) },
                        label = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = currency.symbol,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        },
                        selected = selectedCurrency == currency,
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAccountsHeader() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.margin.medium),
        ) {
            // Preview with null selection (Original Values)
            AccountsHeader(
                totalAmountInSelectedOrDefaultCurrency = "25,430.00",
                defaultCurrency = UiCurrency.dummy,
                selectedCurrency = null,
                totalAccounts = 5,
                userAccountsCurrencies =
                    listOf(
                        UiCurrency.dummy,
                        UiCurrency.dummy.copy(id = "EUR", name = "Euro", symbol = "€"),
                        UiCurrency.dummy.copy(id = "GBP", name = "British Pound", symbol = "£"),
                    ),
                onCurrencyClick = {},
            )

            // Preview with currency selection
            AccountsHeader(
                totalAmountInSelectedOrDefaultCurrency = "23,120.50",
                defaultCurrency = UiCurrency.dummy,
                selectedCurrency = UiCurrency.dummy.copy(id = "EUR", name = "Euro", symbol = "€"),
                totalAccounts = 3,
                userAccountsCurrencies =
                    listOf(
                        UiCurrency.dummy,
                        UiCurrency.dummy.copy(id = "EUR", name = "Euro", symbol = "€"),
                    ),
                onCurrencyClick = {},
            )
        }
    }
}
