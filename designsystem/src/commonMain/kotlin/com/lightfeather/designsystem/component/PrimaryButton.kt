package com.lightfeather.designsystem.component

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )

}


@Preview
@Composable
private fun PreviewPrimaryButton() {
    PrimaryButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.fillMaxWidth(),
        enabled = true
    ) {
        Text("Primary Button")
    }

}