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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.shadow.Shadow
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
    onClick: () -> Unit,
    onTransfer: () -> Unit,
    onEdit: () -> Unit,
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
                    AppTheme.shapes.medium,
                ).dropShadow(
                    shape = AppTheme.shapes.medium,
                    shadow = Shadow(radius = AppTheme.dimens.elevation.component.card),
                ),
        interactionSource = interactionSource,
        shape = AppTheme.shapes.medium,
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
            modifier =
                Modifier
                    .padding(AppTheme.dimens.spacing.padding.medium),
        ) {
            Row {
                Column {
                    Row {
                        AppImage(
                            bankAccount.image,
                            contentDescription = bankAccount.name,
                            modifier = Modifier.size(AppTheme.dimens.icon.size.xxLarge),
                            placeholder = Res.drawable.bank,
                            errorPlaceholder = Res.drawable.bank,
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = AppTheme.dimens.spacing.padding.small),
                        ) {
                            Text(
                                text = bankAccount.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                            )
                            bankAccount.description?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Text(
                        text = bankAccount.balance + " " + bankAccount.currency.symbol,
                        modifier =
                            Modifier.padding(
                                horizontal = AppTheme.dimens.spacing.padding.small,
                                vertical = AppTheme.dimens.spacing.padding.tiny,
                            ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // Action buttons - responsive to window size
                BankAccountActions(
                    onTransfer = onTransfer,
                    onEdit = onEdit,
                    windowSize = rememberAppWindowSizeClass(),
                )
            }
        }
    }
}

@Composable
private fun BankAccountActions(
    onTransfer: () -> Unit,
    onEdit: () -> Unit,
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    val isCompact = windowSize == WindowWidthSizeClass.Compact
    val transferInteractionSource = remember { MutableInteractionSource() }
    val editInteractionSource = remember { MutableInteractionSource() }
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
    ) {
        if (isCompact) {
            // Compact: Icons only with styled background
            Surface(
                onClick = onTransfer,
                modifier = Modifier.size(AppTheme.dimens.touchTarget.min),
                shape = AppTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                interactionSource = transferInteractionSource,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = "Transfer",
                        modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    )
                }
            }

            Surface(
                onClick = onEdit,
                modifier =
                    Modifier.size(AppTheme.dimens.touchTarget.min).hoverable(
                        interactionSource = editInteractionSource,
                    ),
                shape = AppTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                interactionSource = editInteractionSource,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    )
                }
            }
        } else {
            // Medium/Expanded: Icons with labels
            Row(
                modifier =
                    Modifier
                        .clip(AppTheme.shapes.small)
                        .clickable(
                            onClick = { onTransfer() },
                            interactionSource = transferInteractionSource,
                        ).padding(AppTheme.dimens.spacing.padding.small),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(AppTheme.dimens.icon.size.large),
                    shape = AppTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SyncAlt,
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                        )
                    }
                }
                Text(
                    text = stringResource(MR.strings.transfer).orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Row(
                modifier =
                    Modifier
                        .clip(AppTheme.shapes.small)
                        .clickable(
                            onClick = { onEdit() },
                            interactionSource = editInteractionSource,
                        ).padding(AppTheme.dimens.spacing.padding.small),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(AppTheme.dimens.icon.size.large),
                    shape = AppTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                        )
                    }
                }
                Text(
                    text = stringResource(MR.strings.edit).orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
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
            )
        }
    }
}
