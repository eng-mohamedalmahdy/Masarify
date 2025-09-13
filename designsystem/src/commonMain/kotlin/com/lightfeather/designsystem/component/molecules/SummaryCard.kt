package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SummaryCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueTextStyle: TextStyle =
        MaterialTheme.typography.headlineMedium.copy(
            color = MaterialTheme.colorScheme.primary,
        ),
    labelTextStyle: TextStyle =
        MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    cardColors: CardColors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    shape: Shape = MaterialTheme.shapes.medium,
    contentPadding: PaddingValues = PaddingValues(AppTheme.dimens.medium),
) {
    Card(
        modifier = modifier,
        colors = cardColors,
        shape = shape,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding).align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                style = valueTextStyle,
            )
            Text(
                text = label,
                style = labelTextStyle,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSummaryCard() {
    AppTheme {
        SummaryCard(
            value = "100",
            label = "Total Items",
        )
    }
}
