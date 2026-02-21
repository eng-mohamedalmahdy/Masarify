package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Balance overview card molecule component
 * Displays total balance with currency selector and financial stats
 *
 * @param totalBalance The total balance amount formatted as string
 * @param selectedCurrency The currently selected currency
 * @param availableCurrencies List of available currencies for selection
 * @param income Income amount formatted as string
 * @param expense Expense amount formatted as string
 * @param netBalance Net balance amount formatted as string
 * @param onCurrencySelect Callback when currency is selected
 * @param modifier Modifier for the root container
 */
@Suppress("LongMethod")
@Composable
fun BalanceOverviewCard(
    totalBalance: String,
    selectedCurrency: UiCurrency?,
    availableCurrencies: List<UiCurrency>,
    income: String,
    expense: String,
    netBalance: String,
    onCurrencySelect: (UiCurrency) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    width = AppTheme.dimens.hairline,
                    color = AppTheme.colors.secondary,
                    shape = AppTheme.shapes.extraLarge,
                ),
        shape = AppTheme.shapes.extraLarge,
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level2,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        AppTheme.colors.primary,
                                        AppTheme.colors.primary.copy(alpha = GRADIENT_END_ALPHA),
                                    ),
                            ),
                    ).padding(AppTheme.dimens.large),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.large),
            ) {
                // Total Balance Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                ) {
                    // Label + Currency Selector Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "TOTAL BALANCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.secondary.copy(alpha = LABEL_ALPHA),
                            letterSpacing = TEXT_LETTER_SPACING,
                        )

                        // Currency Dropdown Trigger
                        Box {
                            Row(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(AppTheme.dimens.radius.small))
                                        .clickable { currencyDropdownExpanded = true }
                                        .background(Color.White.copy(alpha = DROPDOWN_BG_ALPHA))
                                        .border(
                                            width = AppTheme.dimens.hairline,
                                            color = Color.White.copy(alpha = DROPDOWN_BORDER_ALPHA),
                                            shape = RoundedCornerShape(AppTheme.dimens.radius.small),
                                        ).padding(
                                            horizontal = AppTheme.dimens.medium,
                                            vertical = AppTheme.dimens.extraSmall,
                                        ),
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = selectedCurrency?.getLocalizedName().orEmpty(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )

                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Select currency",
                                    tint = Color.White,
                                    modifier = Modifier.size(AppTheme.dimens.icon.size.xSmall),
                                )
                            }

                            // Dropdown Menu
                            DropdownMenu(
                                expanded = currencyDropdownExpanded,
                                onDismissRequest = { currencyDropdownExpanded = false },
                            ) {
                                availableCurrencies.forEach { currency ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = currency.getLocalizedName(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                )

                                                Text(
                                                    text = currency.symbol,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        },
                                        onClick = {
                                            onCurrencySelect(currency)
                                            currencyDropdownExpanded = false
                                        },
                                        leadingIcon =
                                            if (currency.id == selectedCurrency?.id) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            } else {
                                                null
                                            },
                                    )
                                }
                            }
                        }
                    }

                    // Balance Amount
                    Text(
                        text = "${selectedCurrency?.symbol.orEmpty()}$totalBalance",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }

                // Divider
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTheme.dimens.small)
                            .background(Color.White.copy(alpha = DIVIDER_ALPHA))
                            .size(height = AppTheme.dimens.hairline, width = AppTheme.dimens.massive),
                )

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatItem(
                        icon = Icons.Default.ArrowDownward,
                        label = stringResource(MR.strings.income).orEmpty(),
                        amount = "${selectedCurrency?.symbol}$income",
                        iconColor = AppTheme.colors.success,
                        backgroundColor = AppTheme.colors.success.copy(alpha = STAT_BG_ALPHA),
                    )

                    StatItem(
                        icon = Icons.Default.ArrowUpward,
                        label = stringResource(MR.strings.expense).orEmpty(),
                        amount = "${selectedCurrency?.symbol}$expense",
                        iconColor = MaterialTheme.colorScheme.error,
                        backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = STAT_BG_ALPHA),
                    )

                    StatItem(
                        icon = null,
                        label = stringResource(MR.strings.net_balance_label).orEmpty(),
                        amount = "+${selectedCurrency?.symbol}$netBalance",
                        iconColor = AppTheme.colors.secondary,
                        backgroundColor = AppTheme.colors.secondary.copy(alpha = STAT_BG_ALPHA),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    amount: String,
    iconColor: Color,
    backgroundColor: Color,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
    ) {
        // Label Badge
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(backgroundColor)
                    .padding(
                        horizontal = AppTheme.dimens.medium,
                        vertical = AppTheme.dimens.extraSmall,
                    ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.xSmall),
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = iconColor,
            )
        }

        // Amount
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

private const val GRADIENT_END_ALPHA = 0.9f
private const val LABEL_ALPHA = 0.9f
private const val DROPDOWN_BG_ALPHA = 0.1f
private const val DROPDOWN_BORDER_ALPHA = 0.1f
private const val DIVIDER_ALPHA = 0.1f
private const val STAT_BG_ALPHA = 0.1f
private val TEXT_LETTER_SPACING = androidx.compose.ui.unit.TextUnit.Unspecified

@Preview
@Composable
private fun PreviewBalanceOverviewCard() {
    AppTheme {
        BalanceOverviewCard(
            totalBalance = "12,450.00",
            selectedCurrency = UiCurrency.dummy,
            availableCurrencies = listOf(UiCurrency.dummy),
            income = "4,000",
            expense = "2,500",
            netBalance = "1,500",
            onCurrencySelect = {},
        )
    }
}
