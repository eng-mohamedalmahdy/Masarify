package tech.lightfeather.designsystem.component.organisms.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.resources.compose.stringResource
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.SecondaryButton
import tech.lightfeather.designsystem.model.UiAccountSnapshot
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiFinancialSession
import tech.lightfeather.designsystem.theme.AppTheme

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartOverDialog(
    accounts: List<UiBankAccount>,
    existingSession: UiFinancialSession? = null,
    onConfirm: (name: String?, snapshots: List<UiAccountSnapshot>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var sessionName by remember(existingSession) {
        mutableStateOf(existingSession?.name ?: "")
    }
    val balanceMap =
        remember(existingSession, accounts) {
            val initial =
                accounts.associate { account ->
                    val existingBalance =
                        existingSession
                            ?.accountSnapshots
                            ?.find { it.accountId == account.id }
                            ?.startingBalance
                            ?: account.balance
                    account.id to mutableStateOf(existingBalance)
                }
            initial
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
                        .padding(AppTheme.dimens.default)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
            ) {
                Text(
                    text = stringResource(MR.strings.start_over),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text(stringResource(MR.strings.financial_checkpoint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                HorizontalDivider()

                Text(
                    text = stringResource(MR.strings.starting_balances),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )

                accounts.forEach { account ->
                    val balanceState = balanceMap[account.id]
                    if (balanceState != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                        ) {
                            Text(
                                text = account.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = balanceState.value,
                                onValueChange = { balanceState.value = it },
                                label = {
                                    Text(account.currency.symbol)
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                ) {
                    SecondaryButton(
                        text = stringResource(MR.strings.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = stringResource(MR.strings.save),
                        onClick = {
                            val snapshots =
                                accounts.mapNotNull { account ->
                                    val balanceText = balanceMap[account.id]?.value ?: return@mapNotNull null
                                    val balance = balanceText.toDoubleOrNull() ?: return@mapNotNull null
                                    UiAccountSnapshot(
                                        accountId = account.id,
                                        accountName = account.name,
                                        accountColor = account.color,
                                        accountLogo = account.image ?: "",
                                        startingBalance = balanceText,
                                        currencySymbol = account.currency.symbol,
                                    )
                                }
                            onConfirm(sessionName.ifBlank { null }, snapshots)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
