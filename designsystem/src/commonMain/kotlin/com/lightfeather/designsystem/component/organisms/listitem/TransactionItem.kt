package com.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.atoms.ImageThumbnail
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.toColorInt
import com.lightfeather.designsystem.util.toDisplayableString
import dev.icerock.moko.resources.compose.stringResource
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

@Suppress("LongMethod", "CyclomaticComplexMethod") // Complexity due to backward-compatible expand/collapse UI
@Composable
fun TransactionItem(
    transaction: UiTransaction,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    attachments: List<UiAttachment> = emptyList(),
    onToggle: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val isExpandable = onToggle != null && (transaction.description.isNotBlank() || transaction.hasAttachment)

    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main collapsed row
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppTheme.dimens.default,
                            vertical = AppTheme.dimens.compact,
                        ),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category indicator (colored circle)
                Box(
                    modifier =
                        Modifier
                            .size(AppTheme.dimens.icon.context.avatar)
                            .clip(CircleShape)
                            .background(Color(transaction.category.color.toColorInt())),
                    contentAlignment = Alignment.Center,
                ) {
                    AppImage(
                        model = transaction.category.image,
                        contentDescription = transaction.category.name,
                        contentScale = ContentScale.Crop,
                        placeholder = Res.drawable.bank,
                    )
                }

                // Transaction details (name, accounts, time)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = transaction.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )

                        if (isExpandable) {
                            // Chevron replaces AttachFile when expandable
                            IconButton(
                                onClick = { onToggle.invoke() },
                                modifier = Modifier.size(AppTheme.dimens.icon.context.avatar),
                            ) {
                                Icon(
                                    imageVector =
                                        if (isExpanded) {
                                            Icons.Default.KeyboardArrowUp
                                        } else {
                                            Icons.Default.KeyboardArrowDown
                                        },
                                    contentDescription = null,
                                    modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                                    tint = MaterialTheme.colorScheme.outline,
                                )
                            }
                        } else if (transaction.hasAttachment && onToggle == null) {
                            // Keep original AttachFile indicator when not expandable
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Has attachment",
                                modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                                tint = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text =
                                    if (transaction.isTransfer && transaction.receiverAccount != null) {
                                        "${transaction.account.localizedName} → ${transaction.receiverAccount.name}"
                                    } else {
                                        transaction.account.localizedName
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            // Small paperclip indicator when expandable mode and has attachment
                            if (transaction.hasAttachment && onToggle != null) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                                    tint = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }

                        Text(
                            text = transaction.dateTime.toDisplayableString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }

                    // Balance row — shown in collapsed only when not expandable (backward compat)
                    if (onToggle == null &&
                        transaction.balanceBefore.isNotEmpty() &&
                        transaction.balanceAfter.isNotEmpty()
                    ) {
                        Text(
                            text = "${transaction.balanceBefore} → ${transaction.balanceAfter}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Amount
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        when (transaction.type) {
                            UiTransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                            UiTransactionType.INCOME -> AppTheme.colors.success
                            UiTransactionType.TRANSFER -> AppTheme.colors.secondary
                        },
                )
            }

            // Expanded section — only rendered when expand mode is active
            if (onToggle != null) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = AppTheme.dimens.default,
                                    end = AppTheme.dimens.default,
                                    bottom = AppTheme.dimens.compact,
                                ),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                    ) {
                        HorizontalDivider()

                        // Description
                        if (transaction.description.isNotBlank()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
                            ) {
                                Text(
                                    text = stringResource(MR.strings.transaction_description_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                                Text(
                                    text = transaction.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        // Category chip
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = transaction.category.getLocalizedName(),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors =
                                AssistChipDefaults.assistChipColors(
                                    containerColor =
                                        Color(transaction.category.color.toColorInt()).copy(alpha = 0.15f),
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        )

                        // Balance before → after
                        if (transaction.balanceBefore.isNotEmpty() && transaction.balanceAfter.isNotEmpty()) {
                            Text(
                                text = "${transaction.balanceBefore} → ${transaction.balanceAfter}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            )
                        }

                        // Attachment thumbnails
                        if (attachments.isNotEmpty()) {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                            ) {
                                attachments.forEach { attachment ->
                                    ImageThumbnail(
                                        imageBytes = attachment.fileContent,
                                        onDelete = null,
                                        contentDescription = attachment.name,
                                    )
                                }
                            }
                        }

                        // Quick action row
                        if (onEdit != null || onDelete != null) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (onEdit != null) {
                                    TextButton(onClick = onEdit) {
                                        Text(stringResource(MR.strings.edit))
                                    }
                                }
                                if (onDelete != null) {
                                    TextButton(onClick = onDelete) {
                                        Text(
                                            text = stringResource(MR.strings.delete),
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionItem() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
            modifier = Modifier.padding(AppTheme.dimens.default),
        ) {
            TransactionItem(
                transaction =
                    UiTransaction.dummy.copy(
                        name = "Coffee Shop",
                        amount = "-$25.00",
                        description = "Morning coffee",
                        hasAttachment = true,
                    ),
            )

            TransactionItem(
                transaction =
                    UiTransaction.dummy.copy(
                        name = "Salary Payment",
                        amount = "+$500.00",
                        description = "Monthly salary",
                        type = UiTransactionType.INCOME,
                        hasAttachment = false,
                    ),
            )

            TransactionItem(
                transaction = UiTransaction.dummyTransfer,
            )

            TransactionItem(
                transaction =
                    UiTransaction.dummy.copy(
                        name = "Grocery Shopping",
                        amount = "$120.50",
                        description = "Weekly shopping",
                        hasAttachment = false,
                    ),
                isExpanded = true,
                onToggle = {},
                onEdit = {},
                onDelete = {},
            )
        }
    }
}
