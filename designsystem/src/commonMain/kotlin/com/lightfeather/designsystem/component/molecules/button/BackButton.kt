package com.lightfeather.designsystem.component.molecules.button

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.*
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
        )
    }
}


@Preview
@Composable
private fun PreviewBackButton() {
    AppTheme {
        BackButton{}
    }
}
