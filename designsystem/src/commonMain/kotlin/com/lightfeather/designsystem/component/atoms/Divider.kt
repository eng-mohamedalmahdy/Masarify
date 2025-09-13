package com.lightfeather.designsystem.component.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.VerticalDivider as MaterialVerticalDivider

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = AppTheme.dimens.hairline,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = AppTheme.dimens.hairline,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    MaterialVerticalDivider(
        modifier =
            modifier
                .fillMaxHeight()
                .width(thickness)
                .background(color),
    )
}

@Preview
@Composable
private fun PreviewDivider() {
    AppTheme {
        Column(
            modifier = Modifier.padding(AppTheme.dimens.default),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
        ) {
            // Horizontal divider
            Text("Above divider")
            Divider()
            Text("Below divider")

            // Thicker divider
            Divider(thickness = AppTheme.dimens.small, color = MaterialTheme.colorScheme.primary)

            // Row with vertical divider
            Row(
                modifier = Modifier.height(AppTheme.dimens.component.textField.heightSmall),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
            ) {
                Text("Left")
                VerticalDivider()
                Text("Right")
            }
        }
    }
}
