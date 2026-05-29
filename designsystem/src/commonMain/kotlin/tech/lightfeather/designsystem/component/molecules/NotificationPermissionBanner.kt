package tech.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.SecondaryButton
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun NotificationPermissionBanner(
    onEnable: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.default),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimens.large),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                Text(
                    text = stringResource(MR.strings.notification_banner_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = stringResource(MR.strings.notification_banner_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AppTheme.dimens.default,
                        end = AppTheme.dimens.default,
                        bottom = AppTheme.dimens.default,
                    ),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
        ) {
            SecondaryButton(
                onClick = onLater,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(MR.strings.notification_banner_later))
            }
            PrimaryButton(
                onClick = onEnable,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(MR.strings.notification_banner_enable))
            }
        }
    }
}
