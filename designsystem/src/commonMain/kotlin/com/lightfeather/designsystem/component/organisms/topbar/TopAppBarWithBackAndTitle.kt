package com.lightfeather.designsystem.component.organisms.topbar

import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.lightfeather.designsystem.component.molecules.button.BackButton
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBarWithBackAndFullTitle(
    title: String,
    modifier: Modifier = Modifier,
    supportingContent : @Composable () -> Unit = {},
    onBackClick: () -> Unit,

) {
    TopAppBar(
        title = title,
        modifier = modifier,
        supportingContent = supportingContent,
        navigationIcon = { BackButton(onClick = onBackClick) }
    )
}


@Preview
@Composable
private fun PreviewTopAppBarWithBackAndTitle() {
    AppTheme {
        TopAppBarWithBackAndFullTitle(
            title = "Preview Title",
            onBackClick = {}
        )
    }
}
