package com.lightfeather.designsystem.component.organisms.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.lightfeather.designsystem.component.atoms.ImageThumbnail
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.SecondaryButton
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransactionDetails
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

/**
 * Dialog for adding or editing transactions
 * Supports three transaction types: Expense, Income, and Transfer
 *
 * @param transaction Transaction to edit (null for new transaction)
 * @param initialType Initial transaction type for new transactions
 * @param lockedFromAccount Locked source account (for transfers from account page)
 * @param accounts Available accounts
 * @param categories Available categories
 * @param attachments Current attachments for the transaction
 * @param onDismiss Callback when dialog is dismissed
 * @param onSave Callback when transaction is saved
 * @param onPickImages Callback when user wants to add images
 * @param onDeleteAttachment Callback when user wants to delete an attachment
 * @param modifier Modifier for the dialog
 */
@Suppress("LongMethod", "CyclomaticComplexMethod") // Complex form with multiple transaction types
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
    transaction: UiTransactionDetails?,
    initialType: UiTransactionType = UiTransactionType.EXPENSE,
    lockedFromAccount: UiBankAccount? = null,
    initialAccount: UiBankAccount? = null,
    initialCategory: UiCategory? = null,
    accounts: List<UiBankAccount>,
    categories: List<UiCategory>,
    attachments: List<UiAttachment> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (UiTransactionData) -> Unit,
    onPickImages: () -> Unit,
    onDeleteAttachment: (UiAttachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Form state
    var selectedType by remember { mutableStateOf(transaction?.type ?: initialType) }
    var name by remember { mutableStateOf(transaction?.name ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.removePrefix("-")?.removePrefix("+") ?: "") }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var selectedAccount by remember {
        mutableStateOf<UiBankAccount?>(lockedFromAccount ?: transaction?.account ?: initialAccount)
    }
    var selectedCategory by remember {
        mutableStateOf<UiCategory?>(initialCategory ?: transaction?.categories?.firstOrNull())
    }
    var selectedTargetAccount by remember { mutableStateOf<UiBankAccount?>(transaction?.receiverAccount) }
    var transferFee by remember { mutableStateOf(transaction?.transferFee ?: "0") }

    // Validation
    val isValid =
        name.isNotBlank() &&
            amount.isNotBlank() &&
            amount.toDoubleOrNull() != null &&
            selectedAccount != null &&
            when (selectedType) {
                UiTransactionType.EXPENSE, UiTransactionType.INCOME -> selectedCategory != null
                UiTransactionType.TRANSFER ->
                    selectedTargetAccount != null &&
                        selectedTargetAccount != selectedAccount &&
                        transferFee.toDoubleOrNull() != null
            }

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
                        text = if (transaction == null) stringResource(MR.strings.add_transaction) else stringResource(
                            MR.strings.edit_transaction
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(MR.strings.close),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))

                // Transaction Type Tabs
                TabRow(
                    selectedTabIndex = UiTransactionType.entries.indexOf(selectedType),
                ) {
                    UiTransactionType.entries.forEach { type ->
                        Tab(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            text = {
                                Text(
                                    when (type) {
                                        UiTransactionType.EXPENSE -> stringResource(MR.strings.expense)
                                        UiTransactionType.INCOME -> stringResource(MR.strings.income)
                                        UiTransactionType.TRANSFER -> stringResource(MR.strings.transfer)
                                    },
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.large))

                // Scrollable form content
                Column(
                    modifier =
                        Modifier
                            // Use fill = true to ensure the scrollable content receives bounded height constraints
                            .weight(1f, fill = true)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
                ) {
                    // Common Fields

                    // Transaction Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(MR.strings.transaction_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = name.isBlank(),
                        supportingText =
                            if (name.isBlank()) {
                                { Text(stringResource(MR.strings.transaction_name_required)) }
                            } else {
                                null
                            },
                    )

                    // Amount
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(stringResource(MR.strings.enter_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = amount.isBlank() || amount.toDoubleOrNull() == null,
                        supportingText =
                            if (amount.isBlank()) {
                                { Text(stringResource(MR.strings.transaction_amount_required)) }
                            } else if (amount.toDoubleOrNull() == null) {
                                { Text(stringResource(MR.strings.transaction_amount_invalid)) }
                            } else {
                                null
                            },
                    )

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(MR.strings.transaction_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                    )

                    // Attachments Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(MR.strings.attachments),
                                style = MaterialTheme.typography.labelLarge,
                            )

                            TextButton(onClick = onPickImages) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = stringResource(MR.strings.add_attachment),
                                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                                )
                                Spacer(modifier = Modifier.size(AppTheme.dimens.extraSmall))
                                Text(stringResource(MR.strings.add_attachment))
                            }
                        }

                        if (attachments.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                            ) {
                                items(
                                    items = attachments,
                                ) { attachment ->
                                    ImageThumbnail(
                                        imageBytes = attachment.fileContent,
                                        onDelete = { onDeleteAttachment(attachment) },
                                        contentDescription = attachment.name,
                                    )
                                }
                            }

                            Text(
                                text = if (attachments.size == 1) {
                                    stringResource(MR.strings.attachments_count_singular)
                                } else {
                                    stringResource(MR.strings.attachments_count_plural, attachments.size)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }

                    // Type-Specific Fields
                    when (selectedType) {
                        UiTransactionType.EXPENSE -> {
                            ExpenseFields(
                                selectedAccount = selectedAccount,
                                selectedCategory = selectedCategory,
                                accounts = accounts,
                                categories = categories.filter { it.name != "Transfer" },
                                onAccountSelected = { selectedAccount = it },
                                onCategorySelected = { selectedCategory = it },
                            )
                        }

                        UiTransactionType.INCOME -> {
                            IncomeFields(
                                selectedAccount = selectedAccount,
                                selectedCategory = selectedCategory,
                                accounts = accounts,
                                categories = categories.filter { it.name != "Transfer" },
                                onAccountSelected = { selectedAccount = it },
                                onCategorySelected = { selectedCategory = it },
                            )
                        }

                        UiTransactionType.TRANSFER -> {
                            TransferFields(
                                selectedFromAccount = selectedAccount,
                                selectedToAccount = selectedTargetAccount,
                                transferFee = transferFee,
                                accounts = accounts,
                                isFromAccountLocked = lockedFromAccount != null,
                                onFromAccountSelected = { selectedAccount = it },
                                onToAccountSelected = { selectedTargetAccount = it },
                                onTransferFeeChanged = { transferFee = it },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.large))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                ) {
                    SecondaryButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(MR.strings.cancel))
                    }

                    PrimaryButton(
                        onClick = {
                            val data =
                                UiTransactionData(
                                    id = transaction?.id,
                                    type = selectedType,
                                    name = name,
                                    amount = amount,
                                    description = description.takeIf { it.isNotBlank() },
                                    dateTime =
                                        transaction?.dateTime
                                            ?: Clock.System
                                                .now()
                                                .toLocalDateTime(TimeZone.currentSystemDefault()),
                                    account = selectedAccount!!,
                                    category = selectedCategory,
                                    targetAccount = selectedTargetAccount,
                                    transferFee = transferFee.takeIf { selectedType == UiTransactionType.TRANSFER },
                                    attachments = attachments,
                                )
                            onSave(data)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid,
                    ) {
                        Text(if (transaction == null) stringResource(MR.strings.add) else stringResource(MR.strings.save))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseFields(
    selectedAccount: UiBankAccount?,
    selectedCategory: UiCategory?,
    accounts: List<UiBankAccount>,
    categories: List<UiCategory>,
    onAccountSelected: (UiBankAccount) -> Unit,
    onCategorySelected: (UiCategory) -> Unit,
) {
    var accountExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    // Source Account
    ExposedDropdownMenuBox(
        expanded = accountExpanded,
        onExpandedChange = { accountExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedAccount?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(MR.strings.source_account)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedAccount == null,
            supportingText =
                if (selectedAccount == null) {
                    { Text(stringResource(MR.strings.transaction_account_required)) }
                } else {
                    null
                },
        )

        ExposedDropdownMenu(
            expanded = accountExpanded,
            onDismissRequest = { accountExpanded = false },
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onAccountSelected(account)
                        accountExpanded = false
                    },
                )
            }
        }
    }

    // Category
    ExposedDropdownMenuBox(
        expanded = categoryExpanded,
        onExpandedChange = { categoryExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedCategory?.getLocalizedName().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(MR.strings.category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedCategory == null,
            supportingText =
                if (selectedCategory == null) {
                    { Text(stringResource(MR.strings.transaction_category_required)) }
                } else {
                    null
                },
        )

        ExposedDropdownMenu(
            expanded = categoryExpanded,
            onDismissRequest = { categoryExpanded = false },
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.getLocalizedName()) },
                    onClick = {
                        onCategorySelected(category)
                        categoryExpanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IncomeFields(
    selectedAccount: UiBankAccount?,
    selectedCategory: UiCategory?,
    accounts: List<UiBankAccount>,
    categories: List<UiCategory>,
    onAccountSelected: (UiBankAccount) -> Unit,
    onCategorySelected: (UiCategory) -> Unit,
) {
    var accountExpanded by remember { mutableStateOf(false) }
    var sourceExpanded by remember { mutableStateOf(false) }

    // Target Account
    ExposedDropdownMenuBox(
        expanded = accountExpanded,
        onExpandedChange = { accountExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedAccount?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(MR.strings.target_account)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedAccount == null,
            supportingText =
                if (selectedAccount == null) {
                    { Text(stringResource(MR.strings.transaction_account_required)) }
                } else {
                    null
                },
        )

        ExposedDropdownMenu(
            expanded = accountExpanded,
            onDismissRequest = { accountExpanded = false },
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onAccountSelected(account)
                        accountExpanded = false
                    },
                )
            }
        }
    }

    // Income Source
    ExposedDropdownMenuBox(
        expanded = sourceExpanded,
        onExpandedChange = { sourceExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedCategory?.getLocalizedName() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(MR.strings.income_source)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedCategory == null,
            supportingText =
                if (selectedCategory == null) {
                    { Text(stringResource(MR.strings.transaction_source_required)) }
                } else {
                    null
                },
        )

        ExposedDropdownMenu(
            expanded = sourceExpanded,
            onDismissRequest = { sourceExpanded = false },
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.getLocalizedName()) },
                    onClick = {
                        onCategorySelected(category)
                        sourceExpanded = false
                    },
                )
            }
        }
    }
}

@Suppress("CyclomaticComplexMethod") // Complex UI logic for transfer fields
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferFields(
    selectedFromAccount: UiBankAccount?,
    selectedToAccount: UiBankAccount?,
    transferFee: String,
    accounts: List<UiBankAccount>,
    isFromAccountLocked: Boolean,
    onFromAccountSelected: (UiBankAccount) -> Unit,
    onToAccountSelected: (UiBankAccount) -> Unit,
    onTransferFeeChanged: (String) -> Unit,
) {
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    // From Account
    ExposedDropdownMenuBox(
        expanded = fromExpanded && !isFromAccountLocked,
        onExpandedChange = { if (!isFromAccountLocked) fromExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedFromAccount?.name ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = !isFromAccountLocked,
            label = {
                Text(
                    if (isFromAccountLocked) {
                        "${stringResource(MR.strings.from_account)} (${stringResource(MR.strings.locked)})"
                    } else {
                        stringResource(MR.strings.from_account)
                    },
                )
            },
            trailingIcon = {
                if (!isFromAccountLocked) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded)
                } else {
                    null
                }
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedFromAccount == null,
            supportingText =
                if (selectedFromAccount == null) {
                    { Text(stringResource(MR.strings.transaction_account_required)) }
                } else {
                    null
                },
        )

        if (!isFromAccountLocked) {
            ExposedDropdownMenu(
                expanded = fromExpanded,
                onDismissRequest = { fromExpanded = false },
            ) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.name) },
                        onClick = {
                            onFromAccountSelected(account)
                            fromExpanded = false
                        },
                        enabled = account != selectedToAccount,
                    )
                }
            }
        }
    }

    // To Account
    ExposedDropdownMenuBox(
        expanded = toExpanded,
        onExpandedChange = { toExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedToAccount?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(MR.strings.to_account)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            isError = selectedToAccount == null || selectedToAccount == selectedFromAccount,
            supportingText =
                when (selectedToAccount) {
                    null -> {
                        { Text(stringResource(MR.strings.transaction_account_required)) }
                    }
                    selectedFromAccount -> {
                        { Text(stringResource(MR.strings.transfer_accounts_same)) }
                    }
                    else -> null
                },
        )

        ExposedDropdownMenu(
            expanded = toExpanded,
            onDismissRequest = { toExpanded = false },
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onToAccountSelected(account)
                        toExpanded = false
                    },
                    enabled = account != selectedFromAccount,
                )
            }
        }
    }

    // Transfer Fee
    OutlinedTextField(
        value = transferFee,
        onValueChange = onTransferFeeChanged,
        label = { Text(stringResource(MR.strings.transfer_fee)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = transferFee.toDoubleOrNull() == null,
        supportingText =
            if (transferFee.toDoubleOrNull() == null) {
                { Text(stringResource(MR.strings.transfer_fee_invalid)) }
            } else {
                null
            },
    )
}

/**
 * Data class for transaction form submission
 */
data class UiTransactionData(
    val id: String?,
    val type: UiTransactionType,
    val name: String,
    val amount: String,
    val description: String?,
    val dateTime: LocalDateTime,
    val account: UiBankAccount,
    val category: UiCategory?,
    val targetAccount: UiBankAccount?,
    val transferFee: String?,
    val attachments: List<UiAttachment> = emptyList(),
    val attachmentsToDelete: List<String> = emptyList(),
)

@Preview
@Composable
private fun AddEditTransactionDialogPreview() {
    AppTheme {
        AddEditTransactionDialog(
            transaction = null,
            initialType = UiTransactionType.EXPENSE,
            accounts = listOf(UiBankAccount.dummy, UiBankAccount.dummy.copy(id = "2", name = "Savings")),
            categories = listOf(UiCategory.dummy, UiCategory.dummy.copy(id = "2", name = "Food")),
            onDismiss = {},
            onSave = {},
            lockedFromAccount = null,
            attachments = listOf(),
            onPickImages = {},
            onDeleteAttachment = { },
        )
    }
}
