package tech.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.model.TransactionListItem
import tech.lightfeather.designsystem.model.UiFinancialSession
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.toDisplayableString
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun StartOverMarkerItem(
    item: TransactionListItem.StartOverMarker,
    onToggle: () -> Unit,
    onEdit: (UiFinancialSession) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val session = item.session
    val markerColor = AppTheme.colors.primary

    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onToggle,
        shape = RoundedCornerShape(AppTheme.dimens.medium),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.compact),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(AppTheme.dimens.icon.context.avatar)
                            .clip(CircleShape)
                            .background(markerColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.name ?: stringResource(MR.strings.start_over),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = session.timestamp.toDisplayableString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    if (!item.isExpanded) {
                        Text(
                            text = stringResource(MR.strings.tap_to_see_balances),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }

                IconButton(onClick = { onEdit(session) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(MR.strings.edit),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                    )
                }

                IconButton(onClick = { onDelete(session.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(MR.strings.delete),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                    )
                }

                Icon(
                    imageVector =
                        if (item.isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
            }

            AnimatedVisibility(
                visible = item.isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = AppTheme.dimens.compact),
                ) {
                    HorizontalDivider()
                    Text(
                        text = stringResource(MR.strings.starting_balances),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = AppTheme.dimens.small),
                    )

                    session.accountSnapshots.forEach { snapshot ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = AppTheme.dimens.extraSmall),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = snapshot.accountName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.width(AppTheme.dimens.medium))
                            Text(
                                text = "${snapshot.currencySymbol} ${snapshot.startingBalance}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}
