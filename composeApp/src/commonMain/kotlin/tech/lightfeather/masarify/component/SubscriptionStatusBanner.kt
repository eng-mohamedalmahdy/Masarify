package tech.lightfeather.masarify.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun SubscriptionStatusBanner(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(AppTheme.dimens.spacing.padding.medium),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.spacing.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(MR.strings.subscription_free_banner_title),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(MR.strings.subscription_free_banner_message),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TextButton(onClick = onUpgradeClick) {
                Text(stringResource(MR.strings.subscription_upgrade))
            }
        }
    }
}
