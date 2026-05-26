package tech.lightfeather.designsystem.component.molecules.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.testTag
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech.lightfeather.designsystem.MR

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
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("alert_dialog_confirm_button"),
            ) {
                Text(stringResource(MR.strings.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("alert_dialog_dismiss_button"),
            ) {
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
