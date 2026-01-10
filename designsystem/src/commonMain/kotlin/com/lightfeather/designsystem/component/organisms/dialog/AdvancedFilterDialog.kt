package com.lightfeather.designsystem.component.organisms.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.SecondaryButton
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Advanced filter dialog for transaction filtering
 * Supports filtering by accounts, categories, currencies, types, amount range, date range, and text search
 *
 * @param filter Current filter state
 * @param accounts Available accounts for filtering
 * @param categories Available categories for filtering
 * @param currencies Available currencies for filtering
 * @param onDismiss Callback when dialog is dismissed
 * @param onApply Callback when filter is applied
 * @param onSave Callback when filter is saved (optional)
 * @param modifier Modifier for the dialog
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Suppress("LongMethod", "CyclomaticComplexMethod") // Complex UI dialog with multiple filter sections
@Composable
fun AdvancedFilterDialog(
    filter: UiTransactionFilter,
    accounts: List<UiBankAccount>,
    categories: List<UiCategory>,
    currencies: List<UiCurrency>,
    onDismiss: () -> Unit,
    onApply: (UiTransactionFilter) -> Unit,
    onSave: ((String, UiTransactionFilter) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var workingFilter by remember { mutableStateOf(filter) }
    var showSaveDialog by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth(0.95f)
                    .padding(AppTheme.dimens.spacing.padding.medium),
        ) {
            Column(
                modifier = Modifier.padding(AppTheme.dimens.spacing.padding.large),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(MR.strings.advanced_filters),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(MR.strings.cancel),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))

                // Active filters count
                if (workingFilter.getActiveFilterCount() > 0) {
                    Text(
                        text = "${workingFilter.getActiveFilterCount()} active filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
                }

                // Scrollable filter content
                Column(
                    modifier =
                        Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
                ) {
                    // Text Search
                    FilterSection(title = stringResource(MR.strings.filter_text_search)) {
                        OutlinedTextField(
                            value = workingFilter.textSearch ?: "",
                            onValueChange = {
                                workingFilter = workingFilter.copy(textSearch = it.takeIf { text -> text.isNotBlank() })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search by name or description") },
                            singleLine = true,
                        )
                    }

                    HorizontalDivider()

                    // Transaction Types
                    FilterSection(title = stringResource(MR.strings.filter_by_type)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                        ) {
                            UiTransactionType.entries.forEach { type ->
                                FilterChip(
                                    selected = type in workingFilter.transactionTypes,
                                    onClick = {
                                        workingFilter =
                                            workingFilter.copy(
                                                transactionTypes =
                                                    if (type in workingFilter.transactionTypes) {
                                                        workingFilter.transactionTypes - type
                                                    } else {
                                                        workingFilter.transactionTypes + type
                                                    },
                                            )
                                    },
                                    label = { Text(type.name) },
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Accounts
                    if (accounts.isNotEmpty()) {
                        FilterSection(title = stringResource(MR.strings.filter_by_account)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                            ) {
                                accounts.forEach { account ->
                                    FilterChip(
                                        selected = account in workingFilter.accounts,
                                        onClick = {
                                            workingFilter =
                                                workingFilter.copy(
                                                    accounts =
                                                        if (account in workingFilter.accounts) {
                                                            workingFilter.accounts - account
                                                        } else {
                                                            workingFilter.accounts + account
                                                        },
                                                )
                                        },
                                        label = { Text(account.name) },
                                    )
                                }
                            }
                        }

                        HorizontalDivider()
                    }

                    // Categories
                    if (categories.isNotEmpty()) {
                        FilterSection(title = stringResource(MR.strings.filter_by_category)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                            ) {
                                categories.forEach { category ->
                                    FilterChip(
                                        selected = category in workingFilter.categories,
                                        onClick = {
                                            workingFilter =
                                                workingFilter.copy(
                                                    categories =
                                                        if (category in workingFilter.categories) {
                                                            workingFilter.categories - category
                                                        } else {
                                                            workingFilter.categories + category
                                                        },
                                                )
                                        },
                                        label = { Text(category.name) },
                                    )
                                }
                            }
                        }

                        HorizontalDivider()
                    }

                    // Currencies
                    if (currencies.isNotEmpty()) {
                        FilterSection(title = stringResource(MR.strings.filter_by_currency)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                            ) {
                                currencies.forEach { currency ->
                                    FilterChip(
                                        selected = currency in workingFilter.currencies,
                                        onClick = {
                                            workingFilter =
                                                workingFilter.copy(
                                                    currencies =
                                                        if (currency in workingFilter.currencies) {
                                                            workingFilter.currencies - currency
                                                        } else {
                                                            workingFilter.currencies + currency
                                                        },
                                                )
                                        },
                                        label = { Text(currency.symbol) },
                                    )
                                }
                            }
                        }

                        HorizontalDivider()
                    }

                    // Amount Range
                    FilterSection(title = stringResource(MR.strings.filter_by_amount)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                        ) {
                            OutlinedTextField(
                                value = workingFilter.amountRange?.min ?: "",
                                onValueChange = { min ->
                                    workingFilter =
                                        workingFilter.copy(
                                            amountRange =
                                                UiTransactionFilter.UiAmountRange(
                                                    min = min.takeIf { it.isNotBlank() },
                                                    max = workingFilter.amountRange?.max,
                                                ),
                                        )
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text(stringResource(MR.strings.amount_min)) },
                                placeholder = { Text("0.00") },
                                singleLine = true,
                            )

                            OutlinedTextField(
                                value = workingFilter.amountRange?.max ?: "",
                                onValueChange = { max ->
                                    workingFilter =
                                        workingFilter.copy(
                                            amountRange =
                                                UiTransactionFilter.UiAmountRange(
                                                    min = workingFilter.amountRange?.min,
                                                    max = max.takeIf { it.isNotBlank() },
                                                ),
                                        )
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text(stringResource(MR.strings.amount_max)) },
                                placeholder = { Text("999999.99") },
                                singleLine = true,
                            )
                        }
                    }

                    HorizontalDivider()

                    // Attachments
                    FilterSection(title = stringResource(MR.strings.filter_attachments)) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = workingFilter.hasAttachments == null,
                                    onClick = { workingFilter = workingFilter.copy(hasAttachments = null) },
                                )
                                Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.small))
                                Text(stringResource(MR.strings.any_attachment))
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = workingFilter.hasAttachments == true,
                                    onClick = { workingFilter = workingFilter.copy(hasAttachments = true) },
                                )
                                Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.small))
                                Text(stringResource(MR.strings.with_attachments))
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = workingFilter.hasAttachments == false,
                                    onClick = { workingFilter = workingFilter.copy(hasAttachments = false) },
                                )
                                Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.small))
                                Text(stringResource(MR.strings.without_attachments))
                            }
                        }
                    }

                    HorizontalDivider()

                    // Filter Logic
                    FilterSection(title = stringResource(MR.strings.filter_logic)) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = workingFilter.logic == UiTransactionFilter.UiFilterLogic.AND,
                                    onClick = {
                                        workingFilter =
                                            workingFilter.copy(
                                                logic = UiTransactionFilter.UiFilterLogic.AND,
                                            )
                                    },
                                )
                                Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.small))
                                Text(stringResource(MR.strings.all_conditions))
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = workingFilter.logic == UiTransactionFilter.UiFilterLogic.OR,
                                    onClick = {
                                        workingFilter = workingFilter.copy(logic = UiTransactionFilter.UiFilterLogic.OR)
                                    },
                                )
                                Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.small))
                                Text(stringResource(MR.strings.any_condition))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.large))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                ) {
                    // Clear button
                    SecondaryButton(
                        onClick = {
                            workingFilter = UiTransactionFilter.EMPTY
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(MR.strings.clear_filters))
                    }

                    // Save button (if callback provided)
                    if (onSave != null) {
                        SecondaryButton(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.weight(1f),
                            enabled = workingFilter.getActiveFilterCount() > 0,
                        ) {
                            Text(stringResource(MR.strings.save_filter))
                        }
                    }

                    // Apply button
                    PrimaryButton(
                        onClick = {
                            onApply(workingFilter)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(MR.strings.apply_filters))
                    }
                }
            }
        }
    }

    // Save filter dialog (simple input dialog for filter name)
    if (showSaveDialog && onSave != null) {
        var filterName by remember { mutableStateOf("") }

        BasicAlertDialog(
            onDismissRequest = { showSaveDialog = false },
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier.padding(AppTheme.dimens.spacing.padding.large),
                ) {
                    Text(
                        text = stringResource(MR.strings.save_filter),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))

                    OutlinedTextField(
                        value = filterName,
                        onValueChange = { filterName = it },
                        label = { Text(stringResource(MR.strings.filter_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.large))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                    ) {
                        TextButton(
                            onClick = { showSaveDialog = false },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(MR.strings.cancel))
                        }

                        PrimaryButton(
                            onClick = {
                                onSave(filterName, workingFilter)
                                showSaveDialog = false
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = filterName.isNotBlank(),
                        ) {
                            Text(stringResource(MR.strings.save))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
        content()
    }
}

@Preview
@Composable
private fun AdvancedFilterDialogPreview() {
    AppTheme {
        AdvancedFilterDialog(
            filter = UiTransactionFilter.EMPTY,
            accounts = listOf(UiBankAccount.dummy, UiBankAccount.dummy.copy(id = "2", name = "Savings")),
            categories = listOf(UiCategory.dummy, UiCategory.dummy.copy(id = "2", name = "Transport")),
            currencies = listOf(UiCurrency.dummy, UiCurrency.dummy.copy(id = "2", symbol = "EUR")),
            onDismiss = {},
            onApply = {},
            onSave = { _, _ -> },
        )
    }
}
