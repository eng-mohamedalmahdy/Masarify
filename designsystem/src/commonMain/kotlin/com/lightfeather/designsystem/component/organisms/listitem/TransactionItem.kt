package com.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.toColorInt
import com.lightfeather.designsystem.util.toDisplayableString
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionItem(
    transaction: UiTransaction,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
    ) {
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

            // Transaction details (name, category/accounts, time)
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

                    // Attachment indicator
                    if (transaction.hasAttachment) {
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
                    // For transfer, show both accounts; otherwise show category
                    Text(
                        text =
                            if (transaction.isTransfer && transaction.receiverAccount != null) {
                                "${transaction.account.name} → ${transaction.receiverAccount.name}"
                            } else {
                                transaction.account.name
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    Text(
                        text = transaction.dateTime.toDisplayableString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                if (transaction.balanceBefore.isNotEmpty() && transaction.balanceAfter.isNotEmpty()) {
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
            )
        }
    }
}
