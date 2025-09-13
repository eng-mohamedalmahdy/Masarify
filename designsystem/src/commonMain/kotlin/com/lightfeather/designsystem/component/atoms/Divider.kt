package com.lightfeather.designsystem.component.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.VerticalDivider as MaterialVerticalDivider

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
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
    thickness: Dp = 1.dp,
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Horizontal divider
            Text("Above divider")
            Divider()
            Text("Below divider")

            // Thicker divider
            Divider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)

            // Row with vertical divider
            Row(
                modifier = Modifier.height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Left")
                VerticalDivider()
                Text("Right")
            }
        }
    }
}
