package com.lightfeather.designsystem.component.atoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dashboard section header atom component
 * Displays a title with an optional "See All" action link
 *
 * @param title The section title text
 * @param modifier Modifier for the root container
 * @param onSeeAllClick Optional click handler for "See All" action
 */
@Composable
fun DashboardSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        onSeeAllClick?.let { clickHandler ->
            Text(
                text = stringResource(MR.strings.see_all),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.clickable(onClick = clickHandler),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDashboardSectionHeader() {
    AppTheme {
        DashboardSectionHeader(
            title = "Accounts",
            onSeeAllClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewDashboardSectionHeaderWithoutAction() {
    AppTheme {
        DashboardSectionHeader(
            title = "Overview",
        )
    }
}
