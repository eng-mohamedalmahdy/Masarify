package tech.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.atoms.ImageThumbnail
import tech.lightfeather.designsystem.component.molecules.AppImage
import tech.lightfeather.designsystem.component.molecules.button.AppSegmentedButton
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.SegmentedButton
import tech.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.model.UiTransactionDetails
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.designsystem.model.getBankLocalizedName
import tech.lightfeather.designsystem.model.getLocalizedName
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.parseColor
import kotlin.time.Clock
import tech.lightfeather.designsystem.component.organisms.topbar.TopAppBar as DesignTopAppBar

private val MONTH_ABBREVIATIONS =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/**
 * Full-screen page for adding or editing transactions.
 * Layout mirrors the Stitch design: amount card → type tabs → form fields.
 *
 * Attachment state is owned locally to work around navigation3 entry caching.
 *
 * @param transaction Transaction to edit (null for new transaction)
 * @param initialType Initial transaction type for new transactions
 * @param lockedFromAccount Locked source account (for transfers from account page)
 * @param initialAccount Default account to pre-select
 * @param initialCategory Default category to pre-select
 * @param accounts Available accounts for selection
 * @param categories Available categories for selection
 * @param initialAttachments Pre-loaded attachments (used for edit mode)
 * @param onBack Callback when navigating back
 * @param onSave Callback when transaction is saved with form data
 * @param onPickImages Suspend callback that opens image picker and returns picked images
 * @param modifier Modifier for the page
 */
@Suppress("LongMethod", "CyclomaticComplexMethod", "LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionPageContent(
    transaction: UiTransactionDetails?,
    initialType: UiTransactionType = UiTransactionType.EXPENSE,
    lockedFromAccount: UiBankAccount? = null,
    initialAccount: UiBankAccount? = null,
    initialCategory: UiCategory? = null,
    accounts: List<UiBankAccount>,
    categories: List<UiCategory>,
    initialAttachments: List<UiAttachment> = emptyList(),
    onBack: () -> Unit,
    onSave: (UiTransactionData) -> Unit,
    onPickImages: suspend () -> List<UiAttachment>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedType by remember { mutableStateOf(transaction?.type ?: initialType) }
    var name by remember { mutableStateOf(transaction?.name ?: "") }
    var amount by remember {
        mutableStateOf(transaction?.amount?.removePrefix("-")?.removePrefix("+") ?: "")
    }
    var description by remember { mutableStateOf(transaction?.description ?: "") }
    var selectedAccount by remember {
        mutableStateOf<UiBankAccount?>(lockedFromAccount ?: transaction?.account ?: initialAccount)
    }
    var selectedCategory by remember {
        mutableStateOf<UiCategory?>(initialCategory ?: transaction?.categories?.firstOrNull())
    }
    var selectedTargetAccount by remember { mutableStateOf<UiBankAccount?>(transaction?.receiverAccount) }
    var transferFee by remember { mutableStateOf(transaction?.transferFee ?: "0") }
    var selectedDate by remember {
        mutableStateOf(
            transaction?.dateTime ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Attachment state owned locally — bypasses navigation3 entry caching
    var localAttachments by remember(transaction?.id) { mutableStateOf(initialAttachments) }
    val deletedAttachmentIds = remember(transaction?.id) { mutableListOf<String>() }

    // Expand state for account/category selectors
    var accountSelectorExpanded by remember { mutableStateOf(selectedAccount == null) }
    var targetAccountSelectorExpanded by remember { mutableStateOf(selectedTargetAccount == null) }
    var categorySelectorExpanded by remember { mutableStateOf(selectedCategory == null) }

    val amountColor =
        when (selectedType) {
            UiTransactionType.EXPENSE -> MaterialTheme.colorScheme.error
            UiTransactionType.INCOME -> AppTheme.colors.success
            UiTransactionType.TRANSFER -> MaterialTheme.colorScheme.primary
        }

    val isTransferValid =
        selectedType == UiTransactionType.TRANSFER &&
            selectedTargetAccount != null &&
            selectedTargetAccount != selectedAccount &&
            transferFee.toDoubleOrNull() != null
    val isCategoryValid = selectedType != UiTransactionType.TRANSFER && selectedCategory != null
    val isValid =
        name.isNotBlank() &&
            amount.isNotBlank() &&
            amount.toDoubleOrNull() != null &&
            selectedAccount != null &&
            (isCategoryValid || isTransferValid)

    val pageTitle =
        if (transaction == null) {
            stringResource(MR.strings.add_transaction)
        } else {
            stringResource(MR.strings.edit_transaction)
        }

    fun buildData(): UiTransactionData? {
        val account = selectedAccount ?: return null
        return UiTransactionData(
            id = transaction?.id,
            type = selectedType,
            name = name,
            amount = amount,
            description = description.takeIf { it.isNotBlank() },
            dateTime = selectedDate,
            account = account,
            category = selectedCategory,
            targetAccount = selectedTargetAccount,
            transferFee = transferFee.takeIf { selectedType == UiTransactionType.TRANSFER },
            attachments = localAttachments,
            attachmentsToDelete = deletedAttachmentIds.toList(),
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DesignTopAppBar(
                title = pageTitle,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(MR.strings.cancel),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            buildData()?.let {
                                onSave(it)
                                onBack()
                            }
                        },
                        enabled = isValid,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(MR.strings.save),
                            tint =
                                if (isValid) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                                },
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
        ) {
            // ── Amount card ──────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.elevation.level2),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppTheme.dimens.extraLarge,
                                vertical = AppTheme.dimens.large,
                            ),
                ) {
                    Text(
                        text = stringResource(MR.strings.transaction_amount_label).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.size(AppTheme.dimens.medium))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = selectedAccount?.currency?.symbol ?: "$",
                            style =
                                MaterialTheme.typography.headlineMedium.copy(
                                    color = amountColor,
                                    fontWeight = FontWeight.Light,
                                ),
                            modifier = Modifier.padding(bottom = AppTheme.dimens.small),
                        )
                        Spacer(modifier = Modifier.size(AppTheme.dimens.extraSmall))
                        BasicTextField(
                            value = amount,
                            onValueChange = { input ->
                                val filtered = input.filter { c -> c.isDigit() || c == '.' }
                                if (filtered.count { c -> c == '.' } <= 1) amount = filtered
                            },
                            textStyle =
                                MaterialTheme.typography.displayMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = amountColor,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            cursorBrush = SolidColor(amountColor),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.Center) {
                                    if (amount.isEmpty()) {
                                        Text(
                                            text = "0.00",
                                            style =
                                                MaterialTheme.typography.displayMedium.copy(
                                                    textAlign = TextAlign.Center,
                                                    color = amountColor.copy(alpha = 0.35f),
                                                    fontWeight = FontWeight.SemiBold,
                                                ),
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                    }
                    if (amount.isNotEmpty() && amount.toDoubleOrNull() == null) {
                        Spacer(modifier = Modifier.size(AppTheme.dimens.small))
                        Text(
                            text = stringResource(MR.strings.transaction_amount_invalid),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // ── Type tabs ────────────────────────────────────────────────────
            AppSegmentedButton(
                items =
                    listOf(
                        SegmentedButton.Item(
                            stringResource(MR.strings.expense),
                            UiTransactionType.EXPENSE.name,
                        ),
                        SegmentedButton.Item(
                            stringResource(MR.strings.income),
                            UiTransactionType.INCOME.name,
                        ),
                        SegmentedButton.Item(
                            stringResource(MR.strings.transfer),
                            UiTransactionType.TRANSFER.name,
                        ),
                    ),
                selectedValue = selectedType.name,
                onSelectionChanged = { selectedType = UiTransactionType.valueOf(it) },
                modifier = Modifier.fillMaxWidth(),
            )

            // ── Transaction name ─────────────────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(MR.strings.transaction_name)) },
                placeholder = { Text(stringResource(MR.strings.enter_transaction_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isBlank(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                supportingText =
                    if (name.isBlank()) {
                        { Text(stringResource(MR.strings.transaction_name_required)) }
                    } else {
                        null
                    },
            )

            // ── Date + Time row ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
            ) {
                val monthAbbr = MONTH_ABBREVIATIONS.getOrElse(selectedDate.monthNumber - 1) { "" }
                val dateText = "${selectedDate.dayOfMonth} $monthAbbr ${selectedDate.year}"
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(MR.strings.transaction_date)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = stringResource(MR.strings.transaction_date),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                    enabled = false,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
                val timeText =
                    buildString {
                        val h = selectedDate.hour
                        val m = selectedDate.minute
                        val hour12 = if (h % 12 == 0) 12 else h % 12
                        append(hour12.toString().padStart(2, '0'))
                        append(":")
                        append(m.toString().padStart(2, '0'))
                        append(if (h < 12) " AM" else " PM")
                    }
                OutlinedTextField(
                    value = timeText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(MR.strings.transaction_time)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = stringResource(MR.strings.transaction_time),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                    enabled = false,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }

            // ── Notes ────────────────────────────────────────────────────────
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(MR.strings.notes)) },
                placeholder = { Text(stringResource(MR.strings.enter_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 4,
            )

            // ── Account card ─────────────────────────────────────────────────
            val accountLabel =
                when (selectedType) {
                    UiTransactionType.INCOME -> stringResource(MR.strings.deposit_to)
                    UiTransactionType.TRANSFER -> stringResource(MR.strings.source_account)
                    else -> stringResource(MR.strings.source_account)
                }
            AccountDropdownCard(
                label = accountLabel,
                selected = selectedAccount,
                accounts = accounts,
                locked = lockedFromAccount,
                expanded = true,
                onToggle = { accountSelectorExpanded = !accountSelectorExpanded },
                onSelect = {
                    if (lockedFromAccount == null) {
                        selectedAccount = it
                        accountSelectorExpanded = false
                    }
                },
            )

            // ── Category / Transfer target ───────────────────────────────────
            if (selectedType == UiTransactionType.TRANSFER) {
                AccountDropdownCard(
                    label = stringResource(MR.strings.to_account),
                    selected = selectedTargetAccount,
                    accounts = accounts.filter { it != selectedAccount },
                    locked = null,
                    expanded = true,
                    onToggle = { targetAccountSelectorExpanded = !targetAccountSelectorExpanded },
                    onSelect = {
                        selectedTargetAccount = it
                        targetAccountSelectorExpanded = false
                    },
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = transferFee,
                        onValueChange = { transferFee = it },
                        label = { Text(stringResource(MR.strings.transfer_fee)) },
                        placeholder = { Text(stringResource(MR.strings.enter_transfer_fee)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = transferFee.toDoubleOrNull() == null,
                        supportingText =
                            if (transferFee.toDoubleOrNull() == null) {
                                { Text(stringResource(MR.strings.transfer_fee_invalid)) }
                            } else {
                                null
                            },
                    )
                }
            } else {
                val filteredCategories = categories.filter { it.name != "Transfer" }
                val categoryLabel =
                    if (selectedType == UiTransactionType.INCOME) {
                        stringResource(MR.strings.income_source)
                    } else {
                        stringResource(MR.strings.category)
                    }
                CategoryDropdownCard(
                    label = categoryLabel,
                    selected = selectedCategory,
                    categories = filteredCategories,
                    expanded = true,
                    onToggle = { categorySelectorExpanded = !categorySelectorExpanded },
                    onSelect = {
                        selectedCategory = it
                        categorySelectorExpanded = false
                    },
                )
            }

            // ── Attachments ──────────────────────────────────────────────────
            AttachmentsZone(
                attachments = localAttachments,
                onPickImages = {
                    coroutineScope.launch {
                        val picked = onPickImages()
                        localAttachments = localAttachments + picked
                    }
                },
                onDeleteAttachment = { attachment ->
                    localAttachments = localAttachments - attachment
                    if (attachment.id.toIntOrNull()?.let { it > 0 } == true) {
                        deletedAttachmentIds.add(attachment.id)
                    }
                },
            )

            // ── CTA ──────────────────────────────────────────────────────────
            Spacer(modifier = Modifier.size(AppTheme.dimens.medium))
            PrimaryButton(
                onClick = {
                    buildData()?.let {
                        onSave(it)
                        onBack()
                    }
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(MR.strings.post_transaction),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Spacer(modifier = Modifier.size(AppTheme.dimens.default))
        }
    }

    // ── Date picker dialog ───────────────────────────────────────────────────
    if (showDatePicker) {
        val initialMillis =
            remember(selectedDate) {
                selectedDate.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val picked =
                                Instant
                                    .fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                            selectedDate =
                                LocalDateTime(
                                    year = picked.year,
                                    month = picked.month,
                                    dayOfMonth = picked.dayOfMonth,
                                    hour = selectedDate.hour,
                                    minute = selectedDate.minute,
                                    second = 0,
                                    nanosecond = 0,
                                )
                        }
                        showDatePicker = false
                    },
                ) { Text(stringResource(MR.strings.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(MR.strings.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ── Time picker dialog ───────────────────────────────────────────────────
    if (showTimePicker) {
        val timePickerState =
            rememberTimePickerState(
                initialHour = selectedDate.hour,
                initialMinute = selectedDate.minute,
                is24Hour = false,
            )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.elevation.level3),
            ) {
                Column(
                    modifier = Modifier.padding(AppTheme.dimens.large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
                ) {
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(MR.strings.cancel))
                        }
                        TextButton(
                            onClick = {
                                selectedDate =
                                    LocalDateTime(
                                        year = selectedDate.year,
                                        month = selectedDate.month,
                                        dayOfMonth = selectedDate.dayOfMonth,
                                        hour = timePickerState.hour,
                                        minute = timePickerState.minute,
                                        second = 0,
                                        nanosecond = 0,
                                    )
                                showTimePicker = false
                            },
                        ) { Text(stringResource(MR.strings.save)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDropdownCard(
    label: String,
    selected: UiBankAccount?,
    accounts: List<UiBankAccount>,
    locked: UiBankAccount?,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (UiBankAccount) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.elevation.level0),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (selected != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable(enabled = locked == null) { onToggle() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.huge)
                                .clip(RoundedCornerShape(AppTheme.dimens.medium))
                                .background(parseColor(selected.color.ifEmpty { "#055250" })),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = selected.name.take(1).uppercase(),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    color = androidx.compose.ui.graphics.Color.White,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected.name.getBankLocalizedName(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${selected.currency.symbol} ${selected.balance}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (locked == null) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(MR.strings.select_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (expanded) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                    contentPadding = PaddingValues(horizontal = AppTheme.dimens.extraSmall),
                ) {
                    items(accounts) { account ->
                        FilterChip(
                            selected = account == selected,
                            onClick = { onSelect(account) },
                            enabled = locked == null || account == locked,
                            label = {
                                Column(
                                    modifier = Modifier.padding(vertical = AppTheme.dimens.small),
                                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                                ) {
                                    Text(
                                        account.name.getBankLocalizedName(),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = "${account.currency.symbol} ${account.balance}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            leadingIcon = {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(AppTheme.dimens.medium)
                                            .clip(CircleShape)
                                            .background(parseColor(account.color.ifEmpty { "#888888" })),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdownCard(
    label: String,
    selected: UiCategory?,
    categories: List<UiCategory>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (UiCategory) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.elevation.level0),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (selected != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.huge)
                                .clip(RoundedCornerShape(AppTheme.dimens.medium))
                                .background(parseColor(selected.color.ifEmpty { "#D9B382" })),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppImage(
                            model = selected.image,
                            contentDescription = selected.name,
                            modifier = Modifier.size(AppTheme.dimens.huge),
                        )
                    }
                    Text(
                        text = selected.getLocalizedName(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(MR.strings.select_category),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (expanded) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                    contentPadding = PaddingValues(horizontal = AppTheme.dimens.extraSmall),
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = category == selected,
                            onClick = { onSelect(category) },
                            label = {
                                Text(
                                    text = category.getLocalizedName(),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(vertical = AppTheme.dimens.small),
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(AppTheme.dimens.medium)
                                            .clip(CircleShape)
                                            .background(parseColor(category.color.ifEmpty { "#888888" })),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentsZone(
    attachments: List<UiAttachment>,
    onPickImages: () -> Unit,
    onDeleteAttachment: (UiAttachment) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium)) {
        Text(
            text = stringResource(MR.strings.attachments),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (attachments.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppTheme.dimens.medium))
                        .border(
                            border =
                                BorderStroke(
                                    width = AppTheme.dimens.hairline,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                ),
                            shape = RoundedCornerShape(AppTheme.dimens.medium),
                        ).clickable { onPickImages() }
                        .padding(AppTheme.dimens.extraLarge),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = stringResource(MR.strings.add_attachment),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(AppTheme.dimens.extraLarge),
                    )
                    Text(
                        text = stringResource(MR.strings.add_attachment),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small)) {
                items(attachments) { attachment ->
                    ImageThumbnail(
                        imageBytes = attachment.fileContent,
                        onDelete = { onDeleteAttachment(attachment) },
                        contentDescription = attachment.name,
                    )
                }
            }
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
    }
}

@Preview
@Composable
private fun AddEditTransactionPagePreview() {
    AppTheme {
        AddEditTransactionPageContent(
            transaction = null,
            initialType = UiTransactionType.INCOME,
            accounts = listOf(UiBankAccount.dummy, UiBankAccount.dummy.copy(id = "2", name = "Savings")),
            categories = listOf(UiCategory.dummy, UiCategory.dummy.copy(id = "2", name = "Client Services")),
            onBack = {},
            onSave = {},
            onPickImages = { emptyList() },
        )
    }
}
