package com.lightfeather.masarify.page.currencies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppDropMenu
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import com.lightfeather.designsystem.component.molecules.dialog.AppAlertDialog
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiCurrencyExchangeRate
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CurrenciesPage(viewModel: CurrenciesPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(CurrenciesPageIntent.LoadData)
    }

    CurrenciesPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun CurrenciesPageContent(
    state: CurrenciesPageState,
    onIntent: (CurrenciesPageIntent) -> Unit,
) {
    val currencies by state.allCurrencies.collectAsState(emptyList())
    val exchangeRates by state.exchangeRates.collectAsState(emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = stringResource(MR.strings.currencies),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
                Text(
                    text = stringResource(MR.strings.currencies_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Base Currency Selector
            if (currencies.isNotEmpty()) {
                BaseCurrencySelector(
                    currencies = currencies,
                    selectedCurrency = state.baseCurrency,
                    onCurrencySelected = { onIntent(CurrenciesPageIntent.SelectBaseCurrency(it)) },
                    onEditCurrency = { onIntent(CurrenciesPageIntent.ShowEditCurrencyDialog(it)) },
                    onDeleteCurrency = { onIntent(CurrenciesPageIntent.ShowDeleteDialog(it)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimens.spacing.padding.medium),
                )

                Spacer(modifier = Modifier.height(AppTheme.dimens.default))
            }

            // Exchange Rates Section
            if (currencies.isEmpty()) {
                EmptyState(
                    title = stringResource(MR.strings.no_currencies_title),
                    message = stringResource(MR.strings.no_currencies_message),
                    icon = Icons.Outlined.CurrencyExchange,
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (state.baseCurrency == null) {
                EmptyState(
                    title = stringResource(MR.strings.no_exchange_rates_title),
                    message = stringResource(MR.strings.select_base_currency),
                    icon = Icons.Outlined.CurrencyExchange,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                ExchangeRatesSection(
                    selectedCurrency = state.baseCurrency,
                    exchangeRates = exchangeRates,
                    isEditMode = state.isEditMode,
                    isAutoSyncEnabled = state.isAutoSyncEnabled,
                    onToggleEditMode = { onIntent(CurrenciesPageIntent.ToggleEditMode(it)) },
                    onSaveRates = { rates -> onIntent(CurrenciesPageIntent.SaveExchangeRates(rates)) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { onIntent(CurrenciesPageIntent.ShowAddCurrencyDialog(true)) },
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(MR.strings.add_currency),
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppTheme.dimens.default),
        )
    }

    // Dialogs
    if (state.showAddEditDialog) {
        AddEditCurrencyDialog(
            currency = state.editingCurrency,
            onDismiss = { onIntent(CurrenciesPageIntent.HideDialogs) },
            onConfirm = { name, symbol ->
                if (state.editingCurrency != null) {
                    onIntent(CurrenciesPageIntent.UpdateCurrency(state.editingCurrency, name, symbol))
                } else {
                    onIntent(CurrenciesPageIntent.CreateCurrency(name, symbol))
                }
            },
        )
    }

    if (state.showDeleteDialog && state.deletingCurrency != null) {
        AppAlertDialog(
            onDismissRequest = { onIntent(CurrenciesPageIntent.HideDialogs) },
            title = stringResource(MR.strings.delete_currency_dialog_title),
            message =
                stringResource(
                    MR.strings.delete_currency_dialog_description_with_name,
                    state.deletingCurrency.getLocalizedName(),
                ),
            onConfirm = {
                onIntent(CurrenciesPageIntent.DeleteCurrency(state.deletingCurrency))
            },
        )
    }
}

@Composable
private fun BaseCurrencySelector(
    currencies: List<UiCurrency>,
    selectedCurrency: UiCurrency?,
    onCurrencySelected: (UiCurrency) -> Unit,
    onEditCurrency: (UiCurrency) -> Unit,
    onDeleteCurrency: (UiCurrency) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AppDropMenu(
            label = stringResource(MR.strings.base_currency),
            displayedValue =
                selectedCurrency?.let { "${it.getLocalizedName()} (${it.symbol})" }
                    ?: stringResource(MR.strings.select_base_currency),
            items = currencies,
            onSelectedItem = onCurrencySelected,
            contentRow = { currency ->
                Text(
                    text = "${currency.getLocalizedName()} (${currency.symbol})",
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        // Action buttons for selected currency
        if (selectedCurrency != null && selectedCurrency.isDefault.not()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.dimens.small),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                TextButton(
                    onClick = { onEditCurrency(selectedCurrency) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = AppTheme.dimens.tiny),
                    )
                    Text(stringResource(MR.strings.edit_currency))
                }

                TextButton(
                    onClick = { onDeleteCurrency(selectedCurrency) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.padding(end = AppTheme.dimens.tiny),
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = stringResource(MR.strings.delete_currency_dialog_title),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExchangeRatesSection(
    selectedCurrency: UiCurrency?,
    exchangeRates: List<UiCurrencyExchangeRate>,
    isEditMode: Boolean,
    isAutoSyncEnabled: Boolean,
    onToggleEditMode: (Boolean) -> Unit,
    onSaveRates: (List<UiCurrencyExchangeRate>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editedRates = remember { mutableStateMapOf<String, Pair<Double, Boolean>>() }

    Column(modifier = modifier) {
        // Auto-sync info banner
        if (isAutoSyncEnabled) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimens.spacing.padding.medium)
                        .padding(top = AppTheme.dimens.spacing.padding.small),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(MR.strings.auto_sync_rates_banner),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Sticky Header with Edit/Save Controls
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.default),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(MR.strings.exchange_rates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            if (isEditMode) {
                Row {
                    TextButton(onClick = {
                        editedRates.clear()
                        onToggleEditMode(false)
                    }) {
                        Text(stringResource(MR.strings.cancel_edit))
                    }
                    Spacer(modifier = Modifier.width(AppTheme.dimens.small))
                    TextButton(onClick = {
                        val updatedRatesMap = mutableMapOf<String, UiCurrencyExchangeRate>()

                        // Process all edited rates
                        editedRates.forEach { (rateId, editedValue) ->
                            val (newRate, updateInverse) = editedValue
                            val originalRate = exchangeRates.find { it.id == rateId }

                            if (originalRate != null && originalRate.fromCurrency.id != originalRate.toCurrency.id) {
                                // Update the original rate
                                updatedRatesMap[rateId] = originalRate.copy(rate = newRate)

                                // If updateInverse is true, also update the inverse rate
                                if (updateInverse) {
                                    val inverseRateId = "${originalRate.toCurrency.id}_${originalRate.fromCurrency.id}"
                                    val inverseRate = exchangeRates.find { it.id == inverseRateId }

                                    if (inverseRate != null) {
                                        updatedRatesMap[inverseRateId] = inverseRate.copy(rate = 1.0 / newRate)
                                    } else {
                                        // Create inverse rate if it doesn't exist
                                        updatedRatesMap[inverseRateId] =
                                            UiCurrencyExchangeRate(
                                                id = inverseRateId,
                                                fromCurrency = originalRate.toCurrency,
                                                toCurrency = originalRate.fromCurrency,
                                                rate = 1.0 / newRate,
                                            )
                                    }
                                }
                            }
                        }

                        // Build final list: edited rates + unchanged rates, excluding self-referential rates
                        val finalRates =
                            (
                                exchangeRates.map { rate ->
                                    updatedRatesMap[rate.id] ?: rate
                                } +
                                    updatedRatesMap.values.filter { newRate ->
                                        exchangeRates.none { it.id == newRate.id }
                                    }
                            ).filter { it.fromCurrency.id != it.toCurrency.id }

                        onSaveRates(finalRates)
                    }) {
                        Text(stringResource(MR.strings.save_rates))
                    }
                }
            } else if (!isAutoSyncEnabled || selectedCurrency?.isDefault == false) {
                TextButton(onClick = { onToggleEditMode(true) }) {
                    Text(stringResource(MR.strings.edit_rates))
                }
            }
        }

        // Exchange Rates List
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppTheme.dimens.spacing.padding.medium),
        ) {
            items(
                items = exchangeRates.filter { it.fromCurrency.id != it.toCurrency.id },
            ) { rate ->
                val isRateLocked =
                    isAutoSyncEnabled &&
                        rate.fromCurrency.isoCode != null &&
                        rate.toCurrency.isoCode != null
                ExchangeRateItem(
                    exchangeRate = rate,
                    isEditMode = isEditMode && !isRateLocked,
                    isLocked = isRateLocked,
                    editedValue = editedRates[rate.id],
                    onRateChanged = { newRate, updateInverse ->
                        editedRates[rate.id] = Pair(newRate, updateInverse)
                    },
                    modifier = Modifier.padding(vertical = AppTheme.dimens.spacing.padding.tiny),
                )
            }
        }
    }
}

@Composable
private fun ExchangeRateItem(
    exchangeRate: UiCurrencyExchangeRate,
    isEditMode: Boolean,
    isLocked: Boolean,
    editedValue: Pair<Double, Boolean>?,
    onRateChanged: (Double, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rateText by remember(exchangeRate, editedValue) {
        mutableStateOf((editedValue?.first ?: exchangeRate.rate).toString())
    }
    var updateInverse by remember(editedValue) {
        mutableStateOf(editedValue?.second ?: true)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        if (isEditMode) {
            // Edit Mode - Vertical Layout
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.default),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                // Currency Name
                Text(
                    text = "${exchangeRate.toCurrency.getLocalizedName()} (${exchangeRate.toCurrency.symbol})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Rate Input
                TextField(
                    value = rateText,
                    onValueChange = {
                        rateText = it
                        it.toDoubleOrNull()?.let { rate ->
                            onRateChanged(rate, updateInverse)
                        }
                    },
                    label = stringResource(MR.strings.exchange_rates),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Inverse Rate Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = updateInverse,
                        onCheckedChange = {
                            updateInverse = it
                            rateText.toDoubleOrNull()?.let { rate ->
                                onRateChanged(rate, it)
                            }
                        },
                    )
                    Text(
                        text = stringResource(MR.strings.update_inverse_rate),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = AppTheme.dimens.small),
                    )
                }
            }
        } else {
            // Display Mode - Horizontal Layout
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.default),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Currency Name and Symbol
                Text(
                    text = "${exchangeRate.toCurrency.getLocalizedName()} (${exchangeRate.toCurrency.symbol})",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )

                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = AppTheme.dimens.small),
                    )
                }

                // Exchange Rate Value
                Text(
                    text = (((editedValue?.first ?: exchangeRate.rate) * 1000).toInt() / 1000.0).toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
