package com.lightfeather.designsystem.component.molecules.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.lightfeather.designsystem.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppAlertDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        modifier = modifier,
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(MR.strings.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewAppAlertDialog() {
    AppAlertDialog(
        title = "Logout",
        message = "Are you sure you want to logout?",
        onDismissRequest = {},
        onConfirm = {},
    )
}
