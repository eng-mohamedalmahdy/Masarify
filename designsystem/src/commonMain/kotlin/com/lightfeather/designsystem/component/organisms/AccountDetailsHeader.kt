package com.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.component.molecules.SummaryCard
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.theme.rememberAppWindowSizeClass
import com.lightfeather.designsystem.util.toColorInt
import dev.icerock.moko.resources.compose.stringResource
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountDetailsHeader(
    account: UiBankAccount,
    lastUpdated: String,
    income: String,
    expenses: String,
    transactionCount: String,
    onAddTransaction: () -> Unit,
    onTransferMoney: () -> Unit,
    onEditAccount: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accountColor = Color(account.color.toColorInt())

    val gradientColors =
        listOf(
            accountColor.copy(alpha = 0.8f),
            accountColor.copy(alpha = 0.6f),
        )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = AppTheme.dimens.large, bottomEnd = AppTheme.dimens.large))
                .background(Brush.horizontalGradient(gradientColors))
                .padding(AppTheme.dimens.spacing.padding.medium),
    ) {
        // Account Header Section
        AccountHeaderSection(
            account = account,
            lastUpdated = lastUpdated,
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.large))

        // Action Buttons
        ActionButtonsRow(
            onAddTransaction = onAddTransaction,
            onTransferMoney = onTransferMoney,
            onEditAccount = onEditAccount,
            onDeleteAccount = onDeleteAccount,
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.large))

        // Summary Cards
        SummaryCardsRow(
            income = income,
            expenses = expenses,
            transactionCount = transactionCount,
        )
    }
}

@Composable
private fun AccountHeaderSection(
    account: UiBankAccount,
    lastUpdated: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Account Icon/Logo placeholder
        Box(
            modifier =
                Modifier
                    .size(AppTheme.dimens.huge)
                    .clip(RoundedCornerShape(AppTheme.dimens.medium))
                    .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            AppImage(
                model = account.image,
                contentDescription = account.name,
                modifier = Modifier.size(AppTheme.dimens.large),
                placeholder = Res.drawable.bank,
                errorPlaceholder = Res.drawable.bank,
            )
        }

        Spacer(modifier = Modifier.width(AppTheme.dimens.default))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = account.name,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    ),
            )

            Text(
                text = "${account.currency.symbol}${account.balance}",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
            )

            Text(
                text = stringResource(MR.strings.updated_time, lastUpdated),
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                    ),
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onAddTransaction: () -> Unit,
    onTransferMoney: () -> Unit,
    onEditAccount: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
    ) {
        ActionButton(
            text = stringResource(MR.strings.add_transaction),
            icon = Icons.Outlined.Add,
            onClick = onAddTransaction,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            text = stringResource(MR.strings.transfer),
            icon = Icons.Outlined.SwapHoriz,
            onClick = onTransferMoney,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            text = stringResource(MR.strings.edit),
            icon = Icons.Outlined.Edit,
            onClick = onEditAccount,
            modifier = Modifier.weight(1f),
        )
        ActionButton(
            text = stringResource(MR.strings.delete),
            icon = Icons.Outlined.Delete,
            onClick = onDeleteAccount,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowsClass = rememberAppWindowSizeClass()
    val isCompact = windowsClass == WindowWidthSizeClass.Compact
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(AppTheme.dimens.component.button.height),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = Color.White.copy(alpha = 0.25f),
            ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = AppTheme.dimens.hairline),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(AppTheme.dimens.tiny),
    ) {
        if (isCompact.not()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimens.icon.size.small),
            )
            Spacer(modifier = Modifier.width(AppTheme.dimens.small))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SummaryCardsRow(
    income: String,
    expenses: String,
    transactionCount: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
    ) {
        SummaryCard(
            value = "+$income",
            label = stringResource(MR.strings.income),
            modifier = Modifier.weight(1f),
            valueTextStyle =
                MaterialTheme.typography.titleMedium.copy(
                    color = AppTheme.colors.success,
                    fontWeight = FontWeight.SemiBold,
                ),
            labelTextStyle =
                MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            cardColors =
                CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            contentPadding = PaddingValues(AppTheme.dimens.compact),
        )

        SummaryCard(
            value = "-$expenses",
            label = stringResource(MR.strings.expenses),
            modifier = Modifier.weight(1f),
            valueTextStyle =
                MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                ),
            labelTextStyle =
                MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            cardColors =
                CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            contentPadding = PaddingValues(AppTheme.dimens.compact),
        )

        SummaryCard(
            value = transactionCount,
            label = stringResource(MR.strings.transactions),
            modifier = Modifier.weight(1f),
            valueTextStyle =
                MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                ),
            labelTextStyle =
                MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            cardColors =
                CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            contentPadding = PaddingValues(AppTheme.dimens.compact),
        )
    }
}

@Preview
@Composable
private fun PreviewAccountDetailsHeader() {
    AppTheme {
        AccountDetailsHeader(
            account =
                UiBankAccount.dummy.copy(
                    name = "Main Checking",
                    balance = "1,200.00",
                ),
            lastUpdated = "2 minutes ago",
            income = "$2,000",
            expenses = "$800",
            transactionCount = "25",
            onAddTransaction = {},
            onTransferMoney = {},
            onEditAccount = {},
            onDeleteAccount = {},
        )
    }
}
