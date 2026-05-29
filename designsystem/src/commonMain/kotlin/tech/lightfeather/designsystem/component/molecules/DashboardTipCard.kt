package tech.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.button.TextButton
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun DashboardTipCard(
    tipId: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (title, body) = tipStrings(tipId) ?: return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(MR.strings.tip_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = stringResource(body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(MR.strings.tip_dismiss),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun tipStrings(tipId: Int): Pair<StringResource, StringResource>? =
    when (tipId) {
        0 -> MR.strings.tip_0_add_accounts_title to MR.strings.tip_0_add_accounts_body
        1 -> MR.strings.tip_1_log_expenses_title to MR.strings.tip_1_log_expenses_body
        2 -> MR.strings.tip_2_categories_title to MR.strings.tip_2_categories_body
        3 -> MR.strings.tip_3_analytics_title to MR.strings.tip_3_analytics_body
        4 -> MR.strings.tip_4_sessions_title to MR.strings.tip_4_sessions_body
        5 -> MR.strings.tip_5_cloud_sync_title to MR.strings.tip_5_cloud_sync_body
        6 -> MR.strings.tip_6_export_title to MR.strings.tip_6_export_body
        else -> null
    }
