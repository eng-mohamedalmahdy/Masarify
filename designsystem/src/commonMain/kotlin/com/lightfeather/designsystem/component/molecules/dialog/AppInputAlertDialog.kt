package com.lightfeather.designsystem.component.molecules.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.TextField
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AppInputAlertDialog(
    title: String,
    message: String,
    inputValue: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    inputLabel: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                Text(message)
                TextField(
                    value = inputValue,
                    onValueChange = onValueChange,
                    placeholder = inputLabel,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                    modifier = Modifier,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(inputValue) }) {
                Text(stringResource(MR.strings.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }
        },
        modifier = modifier,
    )
}
