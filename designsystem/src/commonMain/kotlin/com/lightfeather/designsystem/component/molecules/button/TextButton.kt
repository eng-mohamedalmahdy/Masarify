package com.lightfeather.designsystem.component.molecules.button

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    colors: ButtonColors =
        ButtonDefaults.textButtonColors().copy(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
    content: @Composable RowScope.() -> Unit,
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding,
        colors = colors,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewTextButton() {
    AppTheme {
        TextButton(
            onClick = { },
            modifier = Modifier.padding(AppTheme.dimens.default),
            enabled = true,
            content = {
                Text("Text Button")
            },
        )
    }
}
