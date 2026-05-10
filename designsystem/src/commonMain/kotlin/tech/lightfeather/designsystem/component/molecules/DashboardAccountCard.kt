package tech.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.getBankLocalizedName
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.toColorInt
import masarify.designsystem.generated.resources.Res
import masarify.designsystem.generated.resources.bank
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dashboard account card molecule component
 * Compact card displaying account icon, name, and balance
 *
 * @param account The bank account to display
 * @param onClick Click handler for card navigation
 * @param modifier Modifier for the root container
 */
@Composable
fun DashboardAccountCard(
    account: UiBankAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val accountColor =
        runCatching { Color(account.color.toColorInt()) }.getOrElse { surfaceColor }

    Card(
        onClick = onClick,
        modifier =
            modifier
                .width(AppTheme.dimens.massive * 3)
                .height(height = AppTheme.dimens.component.card.minHeight)
                .border(
                    width = AppTheme.dimens.hairline,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = BORDER_ALPHA),
                    shape = AppTheme.shapes.large,
                ),
        shape = AppTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level1,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Account icon
            Box(
                modifier =
                    Modifier
                        .size(AppTheme.dimens.icon.size.large)
                        .clip(CircleShape)
                        .background(accountColor.copy(alpha = ICON_BG_ALPHA)),
                contentAlignment = Alignment.Center,
            ) {
                AppImage(
                    model = account.image,
                    contentDescription = account.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    placeholder = Res.drawable.bank,
                    errorPlaceholder = Res.drawable.bank,
                )
            }

            // Account name and balance
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
            ) {
                Text(
                    text = account.name.getBankLocalizedName(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "${account.balance} ${account.currency.symbol}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private const val BORDER_ALPHA = 0.3f
private const val ICON_BG_ALPHA = 0.1f

@Preview
@Composable
private fun PreviewDashboardAccountCard() {
    AppTheme {
        Box(
            modifier = Modifier.padding(AppTheme.dimens.default),
        ) {
            DashboardAccountCard(
                account = UiBankAccount.dummy,
                onClick = {},
            )
        }
    }
}
