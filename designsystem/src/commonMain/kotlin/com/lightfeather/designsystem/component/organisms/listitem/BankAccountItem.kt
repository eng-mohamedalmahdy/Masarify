package com.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.theme.rememberAppWindowSizeClass
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.designsystem.util.toColorInt
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BankAccountItem(
    bankAccount: UiBankAccount,
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.shapes.medium,
    onClick: () -> Unit,
    onTransfer: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateTransaction: () -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val accountColor = runCatching { Color(bankAccount.color.toColorInt()) }.getOrElse { surfaceColor }
    val interactionSource = remember { MutableInteractionSource() }
    val hoverState by interactionSource.collectIsHoveredAsState()
    val borderColorAnimated by animateColorAsState(
        targetValue = if (hoverState) MaterialTheme.colorScheme.primary else accountColor,
        label = "borderColorAnimated",
    )
    val borderThicknessAnimated = if (hoverState) AppTheme.dimens.border.thin else AppTheme.dimens.elevation.level0
    Card(
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level0,
                hoveredElevation = AppTheme.dimens.elevation.component.card,
            ),
        modifier =
            modifier
                .hoverable(interactionSource)
                .border(
                    borderThicknessAnimated,
                    borderColorAnimated,
                    shape,
                ).dropShadow(
                    shape = shape,
                    shadow = Shadow(radius = AppTheme.dimens.elevation.component.card),
                ),
        interactionSource = interactionSource,
        shape = shape,
        colors =
            CardDefaults.cardColors(
                containerColor = accountColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.dimens.spacing.padding.small),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppImage(
                        bankAccount.image,
                        contentDescription = bankAccount.name,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.xxLarge),
                        placeholder = Res.drawable.bank,
                        errorPlaceholder = Res.drawable.bank,
                    )
                    Column {
                        Row {
                            Text(
                                text = bankAccount.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                            )
                            Text(
                                text = bankAccount.balance + " " + bankAccount.currency.symbol,
                                modifier =
                                    Modifier.padding(start = AppTheme.dimens.spacing.padding.tiny),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        if (!bankAccount.description.isNullOrBlank()) {
                            Text(
                                text = bankAccount.description,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                BankAccountActions(
                    onTransfer = onTransfer,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onCreateTransaction = onCreateTransaction,
                    windowSize = rememberAppWindowSizeClass(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.dimens.spacing.padding.small),
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isCompact: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isCompact) {
        // Compact: Icon only with styled background
        Surface(
            onClick = onClick,
            modifier = modifier.size(AppTheme.dimens.touchTarget.min),
            shape = AppTheme.shapes.small,
            color = containerColor,
            contentColor = contentColor,
            interactionSource = interactionSource,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                )
            }
        }
    } else {
        // Medium/Expanded: Icon with label
        Row(
            modifier =
                modifier
                    .padding(top = AppTheme.dimens.spacing.padding.small)
                    .clip(AppTheme.shapes.small)
                    .clickable(
                        onClick = onClick,
                        interactionSource = interactionSource,
                    ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(AppTheme.dimens.icon.size.large),
                shape = AppTheme.shapes.small,
                color = containerColor,
                contentColor = contentColor,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun BankAccountActions(
    onTransfer: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateTransaction: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val isCompact = windowSize == WindowWidthSizeClass.Compact

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ActionButton(
            icon = Icons.Default.Add,
            label = stringResource(MR.strings.add_transaction).orEmpty(),
            onClick = onCreateTransaction,
            isCompact = isCompact,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        ActionButton(
            icon = Icons.Default.SyncAlt,
            label = stringResource(MR.strings.transfer).orEmpty(),
            onClick = onTransfer,
            isCompact = isCompact,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        ActionButton(
            icon = Icons.Default.Edit,
            label = stringResource(MR.strings.edit).orEmpty(),
            onClick = onEdit,
            isCompact = isCompact,
        )

        ActionButton(
            icon = Icons.Default.Delete,
            label = stringResource(MR.strings.delete).orEmpty(),
            onClick = onDelete,
            isCompact = isCompact,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Preview("ar")
@Composable
private fun PreviewBankAccountItem() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            BankAccountItem(
                UiBankAccount.dummy,
                onClick = {},
                onTransfer = {},
                onEdit = {},
                onDelete = {},
                onCreateTransaction = {},
            )
        }
    }
}
