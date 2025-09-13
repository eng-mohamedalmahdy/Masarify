package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.component.atoms.Icon
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExtendedFloatingActionButton as MaterialSmallFloatingActionButton
import androidx.compose.material3.FloatingActionButton as MaterialFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton as MaterialSmallFloatingActionButton

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
) {
    MaterialFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        content = icon,
    )
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
) {
    FloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = contentColor,
            )
        },
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
    )
}

@Composable
fun SmallFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
) {
    MaterialSmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        content = icon,
    )
}

@Composable
fun ExtendedFloatingActionButton(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
) {
    MaterialSmallFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        icon = icon,
        text = text,
    )
}

@Preview
@Composable
private fun PreviewFloatingActionButton() {
    AppTheme {
        FloatingActionButton(
            onClick = { },
            icon = {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewSmallFloatingActionButton() {
    AppTheme {
        SmallFloatingActionButton(
            onClick = { },
            icon = {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewLargeFloatingActionButton() {
    AppTheme {
        LargeFloatingActionButton(
            onClick = { },
            modifier = Modifier.padding(16.dp),
        ) {
        }
    }
}
