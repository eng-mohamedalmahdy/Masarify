package com.lightfeather.designsystem.component.molecules.listitem

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.toColorInt
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BankAccountItem(
    bankAccount: UiBankAccount,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val accountColor = runCatching { Color(bankAccount.color.toColorInt()) }.getOrElse { surfaceColor }
    val interactionSource = remember { MutableInteractionSource() }
    val hoverState by interactionSource.collectIsHoveredAsState()
    val borderColorAnimated by animateColorAsState(
        targetValue = if (hoverState) MaterialTheme.colorScheme.primary else accountColor,
        label = "borderColorAnimated"
    )
    val borderThicknessAnimated = if (hoverState) AppTheme.dimens.border.thin else 0.dp
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            hoveredElevation = AppTheme.dimens.elevation.component.card,
        ),
        modifier = modifier.hoverable(interactionSource)
            .border(
                borderThicknessAnimated,
                borderColorAnimated,
                AppTheme.shapes.medium
            ).dropShadow(
                shape = AppTheme.shapes.medium,
                shadow = Shadow(radius = AppTheme.dimens.elevation.component.card)
            ),
        interactionSource = interactionSource,
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = accountColor,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(AppTheme.dimens.spacing.padding.medium),
        ) {
            Row {
                AppImage(
                    bankAccount.image,
                    contentDescription = bankAccount.name,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.xxLarge),
                    placeholder = Res.drawable.bank,
                    errorPlaceholder = Res.drawable.bank,
                )
                Column {
                    Text(
                        text = bankAccount.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = AppTheme.dimens.spacing.padding.small),
                        maxLines = 1,
                    )
                    bankAccount.description?.let {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = AppTheme.dimens.spacing.padding.small),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                text = bankAccount.balance + " " + bankAccount.currency.symbol,
                modifier = Modifier.padding(horizontal = AppTheme.dimens.spacing.padding.small),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


@Preview( "ar")
@Composable
private fun PreviewBankAccountItem() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BankAccountItem(UiBankAccount.dummy) {}
        }
    }
}
