package com.lightfeather.masarify.page.createbankaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppDropMenu
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.TextButton
import com.lightfeather.designsystem.component.molecules.dialog.ColorPickerDialog
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.colorToHex
import com.lightfeather.designsystem.util.parseColor
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreateBankAccountPage(
    account: UiBankAccount,
    viewModel: CreateBankAccountPageViewModel =
        koinViewModel(
            key = account.id,
            parameters = { parametersOf(account) },
        ),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val navigationState = rememberNavigationEventState(NavigationEventInfo.None)

    NavigationEventHandler(navigationState) {
        viewModel.resetState()
        onBack()
    }
    CreateBankAccountPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = {
            viewModel.resetState()
            onBack()
        },
    )
}

// Composable UI function with form layout - length is acceptable for UI composition
@Suppress("LongMethod")
@Composable
internal fun CreateBankAccountPageContent(
    state: CreateBankAccountPageState,
    onIntent: (CreateBankAccountPageIntent) -> Unit,
    onBack: () -> Unit,
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(AppTheme.dimens.default)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
    ) {
        // Account Name Field
        TextField(
            value = state.name,
            onValueChange = { onIntent(CreateBankAccountPageIntent.UpdateName(it)) },
            label = stringResource(MR.strings.account_name),
            placeholder = stringResource(MR.strings.enter_account_name),
            modifier = Modifier.fillMaxWidth(),
        )

        // Bank Selection Dropdown
        AppDropMenu(
            label = stringResource(MR.strings.select_bank),
            displayedValue =
                state.selectedBank?.getLocalizedName()
                    ?: stringResource(MR.strings.select_bank),
            items = state.availableBanks,
            onSelectedItem = { bank ->
                onIntent(CreateBankAccountPageIntent.SelectBank(bank))
            },
            searchFunction = { banks, query ->
                banks.filter { bank ->
                    bank.name.contains(query, ignoreCase = true)
                }
            },
            footerContent = { searchQuery ->
                TextButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            onIntent(CreateBankAccountPageIntent.AddNewBank(searchQuery))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(MR.strings.add_new_bank_description),
                        )
                        Text(stringResource(MR.strings.add_new_bank))
                    }
                }
            },
            contentRow = { bank ->
                Text(
                    text = bank.getLocalizedName(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = AppTheme.dimens.small),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        // Description Field
        TextField(
            value = state.description,
            onValueChange = { onIntent(CreateBankAccountPageIntent.UpdateDescription(it)) },
            label = stringResource(MR.strings.description),
            placeholder = stringResource(MR.strings.enter_description_optional),
            modifier = Modifier.fillMaxWidth(),
        )

        // Initial Balance Field (disabled in edit mode)
        TextField(
            value = state.initialBalance,
            onValueChange = { onIntent(CreateBankAccountPageIntent.UpdateInitialBalance(it)) },
            label =
                if (state.inEditMode) {
                    stringResource(MR.strings.current_balance)
                } else {
                    stringResource(MR.strings.initial_balance)
                },
            placeholder = stringResource(MR.strings.balance_placeholder),
            enabled = !state.inEditMode,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            colors =
                OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
        )

        // Color Picker Section
        Column {
            Text(
                text = stringResource(MR.strings.account_color),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.small))

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { showColorPicker = true },
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.dimens.default),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.huge)
                                .clip(RoundedCornerShape(AppTheme.dimens.small))
                                .background(parseColor(state.color))
                                .border(
                                    AppTheme.dimens.extraSmall,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(AppTheme.dimens.small),
                                ),
                    )

                    Text(
                        text = stringResource(MR.strings.tap_to_select_color),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )

                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = stringResource(MR.strings.choose_color),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Currency Dropdown with Search and Add New functionality
        AppDropMenu(
            label = stringResource(MR.strings.currency),
            displayedValue =
                state.currency?.let {
                    "${it.getLocalizedName()} (${it.symbol})"
                } ?: stringResource(MR.strings.select_currency),
            items = state.availableCurrencies,
            onSelectedItem = { currency ->
                onIntent(CreateBankAccountPageIntent.UpdateCurrency(currency))
            },
            searchFunction = { currencies, query ->
                currencies.filter { currency ->
                    currency.name.contains(query, ignoreCase = true) ||
                        currency.symbol.contains(query, ignoreCase = true)
                }
            },
            footerContent = { searchQuery ->
                TextButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            onIntent(
                                CreateBankAccountPageIntent.AddNewCurrency(
                                    UiCurrency("", searchQuery, searchQuery),
                                ),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(MR.strings.add_new_currency_description),
                        )
                        Text(stringResource(MR.strings.add_new_currency))
                    }
                }
            },
            contentRow = { currency ->
                Column {
                    Text(
                        text = currency.getLocalizedName(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = currency.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        // Default Account Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(MR.strings.set_as_default_account),
                style = MaterialTheme.typography.bodyMedium,
            )
            Switch(
                checked = state.isDefault,
                onCheckedChange = { onIntent(CreateBankAccountPageIntent.ToggleDefault) },
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.dimens.large))

        // Submit Button
        PrimaryButton(
            onClick = {
                onIntent(CreateBankAccountPageIntent.Submit)
                onBack()
            },
            enabled = !state.isLoading && state.name.isNotBlank() && state.currency != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text =
                    if (state.inEditMode) {
                        stringResource(MR.strings.update_account)
                    } else {
                        stringResource(MR.strings.create_account)
                    },
            )
        }
    }

    // Color Picker Dialog
    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = parseColor(state.color),
            savedColors = state.savedColors,
            onColorChange = { color ->
                onIntent(CreateBankAccountPageIntent.UpdateColor(colorToHex(color)))
            },
            onDismiss = { showColorPicker = false },
            onConfirm = { color ->
                onIntent(CreateBankAccountPageIntent.UpdateColor(colorToHex(color)))
                onIntent(CreateBankAccountPageIntent.SaveColor(colorToHex(color)))
                showColorPicker = false
            },
        )
    }
}
