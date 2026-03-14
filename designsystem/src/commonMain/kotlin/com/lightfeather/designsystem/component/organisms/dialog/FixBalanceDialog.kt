package com.lightfeather.designsystem.component.organisms.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.SecondaryButton
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.abs

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixBalanceDialog(
    account: UiBankAccount,
    onApply: (account: UiBankAccount, actualBalance: Double, isIncrease: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentBalance = account.balance.replace(Regex("[^\\d.-]"), "").toDoubleOrNull() ?: 0.0
    var actualBalanceInput by remember { mutableStateOf("") }
    var isIncrease by remember { mutableStateOf(true) }

    val actualBalance by remember(actualBalanceInput) {
        derivedStateOf { actualBalanceInput.toDoubleOrNull() }
    }

    val difference by remember(actualBalance, currentBalance) {
        derivedStateOf {
            actualBalance?.let { it - currentBalance }
        }
    }

    // Auto-determine increase/decrease from difference sign
    val autoIsIncrease by remember(difference) {
        derivedStateOf { difference?.let { it >= 0 } ?: true }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.default),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.default),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
            ) {
                // Title
                Text(
                    text = stringResource(MR.strings.fix_balance),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                HorizontalDivider()

                // Account name + current balance (read-only)
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(MR.strings.current_balance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${account.currency.symbol}${account.balance}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Actual balance input
                OutlinedTextField(
                    value = actualBalanceInput,
                    onValueChange = { input ->
                        actualBalanceInput = input
                        // Auto update direction based on difference
                        val diff = input.toDoubleOrNull()?.let { it - currentBalance }
                        if (diff != null) {
                            isIncrease = diff >= 0
                        }
                    },
                    label = { Text(stringResource(MR.strings.actual_balance)) },
                    placeholder = { Text(stringResource(MR.strings.enter_actual_balance)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Difference display
                difference?.let { diff ->
                    val absDiff = abs(diff)
                    val direction = if (diff >= 0) "+" else "-"
                    val diffColor =
                        if (diff >= 0) AppTheme.colors.success else MaterialTheme.colorScheme.error
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(MR.strings.balance_adjustment),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "$direction${account.currency.symbol}$absDiff",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = diffColor,
                        )
                    }
                }

                // Increase / Decrease segmented control
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        onClick = { isIncrease = true },
                        selected = isIncrease,
                    ) {
                        Text(stringResource(MR.strings.balance_increase))
                    }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        onClick = { isIncrease = false },
                        selected = !isIncrease,
                    ) {
                        Text(stringResource(MR.strings.balance_decrease))
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                ) {
                    SecondaryButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(MR.strings.cancel))
                    }
                    PrimaryButton(
                        onClick = {
                            val balance = actualBalance ?: return@PrimaryButton
                            if (balance == currentBalance) return@PrimaryButton
                            onApply(account, balance, autoIsIncrease)
                        },
                        enabled = actualBalance != null && actualBalance != currentBalance,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(MR.strings.apply_adjustment))
                    }
                }
            }
        }
    }
}
