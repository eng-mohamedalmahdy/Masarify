package tech.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.compose.stringResource
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.AppImage
import tech.lightfeather.designsystem.component.molecules.button.SecondaryButton
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiTransactionDetails
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.designsystem.model.getBankLocalizedName
import tech.lightfeather.designsystem.model.getLocalizedName
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.toColorInt
import tech.lightfeather.designsystem.util.toDisplayableString

/**
 * Detailed view of a transaction for display in the detail pane
 * Shows all transaction information including type-specific fields
 *
 * @param transaction Transaction to display
 * @param attachments List of attachments for the transaction
 * @param onEdit Callback when edit button is clicked
 * @param onDelete Callback when delete button is clicked
 * @param onDuplicate Callback when duplicate button is clicked
 * @param modifier Modifier for the component
 */
@Suppress("LongMethod", "CyclomaticComplexMethod") // Detailed UI with multiple transaction types
@Composable
fun TransactionDetailView(
    transaction: UiTransactionDetails,
    attachments: List<UiAttachment> = emptyList(),
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.dimens.spacing.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
    ) {
        // Header with transaction type badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = transaction.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            when (transaction.type) {
                                UiTransactionType.EXPENSE -> MaterialTheme.colorScheme.errorContainer
                                UiTransactionType.INCOME -> AppTheme.colors.success
                                UiTransactionType.TRANSFER -> MaterialTheme.colorScheme.secondaryContainer
                            },
                    ),
            ) {
                Text(
                    text = transaction.typeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = AppTheme.dimens.compact, vertical = AppTheme.dimens.small),
                    color =
                        when (transaction.type) {
                            UiTransactionType.EXPENSE -> MaterialTheme.colorScheme.onErrorContainer
                            UiTransactionType.INCOME -> AppTheme.colors.success
                            UiTransactionType.TRANSFER -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                )
            }
        }

        // Amount
        Text(
            text = transaction.amount,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color =
                when (transaction.type) {
                    UiTransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                    UiTransactionType.INCOME -> AppTheme.colors.success
                    UiTransactionType.TRANSFER -> AppTheme.colors.secondary
                },
        )

        HorizontalDivider()

        // Account Information
        when (transaction.type) {
            UiTransactionType.EXPENSE, UiTransactionType.INCOME -> {
                val accountLabel =
                    if (transaction.type == UiTransactionType.EXPENSE) {
                        stringResource(MR.strings.source_account)
                    } else {
                        stringResource(MR.strings.target_account)
                    }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(AppTheme.dimens.spacing.padding.medium),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppImage(
                            model = transaction.account.image,
                            contentDescription = transaction.account.name,
                            modifier =
                                Modifier
                                    .size(AppTheme.dimens.icon.size.large)
                                    .clip(CircleShape),
                            placeholder = Res.drawable.bank,
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                        ) {
                            Text(
                                text = accountLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            Text(
                                text = transaction.account.name.getBankLocalizedName(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            UiTransactionType.TRANSFER -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(AppTheme.dimens.spacing.padding.medium),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // From Account
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AppImage(
                                model = transaction.account.image,
                                contentDescription = transaction.account.name,
                                modifier =
                                    Modifier
                                        .size(AppTheme.dimens.icon.size.large)
                                        .clip(CircleShape),
                                placeholder = Res.drawable.bank,
                            )
                            Spacer(modifier = Modifier.height(AppTheme.dimens.small))
                            Text(
                                text = transaction.account.name.getBankLocalizedName(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = stringResource(MR.strings.transaction_from),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }

                        // Arrow
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        // To Account
                        transaction.receiverAccount?.let { receiver ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                AppImage(
                                    model = receiver.image,
                                    contentDescription = receiver.name,
                                    modifier =
                                        Modifier
                                            .size(AppTheme.dimens.icon.size.large)
                                            .clip(CircleShape),
                                    placeholder = Res.drawable.bank,
                                )
                                Spacer(modifier = Modifier.height(AppTheme.dimens.small))
                                Text(
                                    text = receiver.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = stringResource(MR.strings.transaction_to),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                    }
                }

                // Transfer Fee
                transaction.transferFee?.let { fee ->
                    DetailItem(
                        label = stringResource(MR.strings.transfer_fee),
                        value = fee,
                    )
                }
            }
        }

        // Category
        FlowRow {
            transaction.categories.forEach { category ->
                DetailItem(
                    label =
                        if (transaction.type == UiTransactionType.INCOME) {
                            stringResource(MR.strings.income_source)
                        } else {
                            stringResource(MR.strings.category)
                        },
                    value = category.getLocalizedName(),
                    icon = {
                        AppImage(
                            model = category.image,
                            contentDescription = category.getLocalizedName(),
                            modifier =
                                Modifier
                                    .size(AppTheme.dimens.icon.size.medium)
                                    .clip(CircleShape)
                                    .background(
                                        Color(
                                            transaction.categories
                                                .firstOrNull()
                                                ?.color
                                                ?.toColorInt() ?: 0xFF0000,
                                        ),
                                    ),
                            placeholder = Res.drawable.bank,
                        )
                    },
                )
            }
        }

        // Date & Time
        DetailItem(
            label = stringResource(MR.strings.transaction_date_time),
            value = transaction.dateTime.toDisplayableString(),
        )

        // Description
        if (transaction.description.isNotBlank()) {
            DetailItem(
                label = stringResource(MR.strings.description),
                value = transaction.description,
            )
        }

        // Attachments
        if (attachments.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = stringResource(MR.strings.attachments),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                    )
                    Text(
                        text = stringResource(MR.strings.attachments_count, attachments.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                AttachmentGrid(
                    attachments = attachments,
                    onDelete = null, // Read-only view, no deletion
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))

        HorizontalDivider()

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
        ) {
            SecondaryButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(MR.strings.edit),
                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                )
                Spacer(modifier = Modifier.size(AppTheme.dimens.small))
                Text(stringResource(MR.strings.edit))
            }

            SecondaryButton(
                onClick = onDuplicate,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(MR.strings.duplicate))
            }

            SecondaryButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(MR.strings.delete),
                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.size(AppTheme.dimens.small))
                Text(
                    text = stringResource(MR.strings.delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    icon: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.invoke()

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview
@Composable
private fun TransactionDetailViewPreview() {
    AppTheme {
        TransactionDetailView(
            transaction = UiTransactionDetails.dummyTransfer,
            onEdit = {},
            onDelete = {},
            onDuplicate = {},
        )
    }
}
