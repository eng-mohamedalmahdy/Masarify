package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Month selector molecule component with navigation
 * Displays current month/year with previous and next month buttons
 *
 * @param selectedMonth The current month/year text (e.g., "January 2026")
 * @param onPreviousMonth Click handler for previous month button
 * @param onNextMonth Click handler for next month button
 * @param modifier Modifier for the root container
 */
@Composable
fun MonthSelector(
    selectedMonth: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .shadow(
                    elevation = AppTheme.dimens.elevation.level1,
                    shape = RoundedCornerShape(percent = 50),
                ).border(
                    width = AppTheme.dimens.hairline,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(percent = 50),
                ),
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AppTheme.dimens.extraSmall),
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.size(AppTheme.dimens.icon.context.avatar),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowLeft,
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                )
            }

            Text(
                text = selectedMonth,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = AppTheme.dimens.small),
            )

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.size(AppTheme.dimens.icon.context.avatar),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMonthSelector() {
    AppTheme {
        MonthSelector(
            selectedMonth = "January 2026",
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}
