package com.lightfeather.masarify.page.currencies

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun DeleteCurrencyDialog(
    currency: UiCurrency,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(MR.strings.delete_currency_dialog_title))
        },
        text = {
            Text(
                text = stringResource(MR.strings.delete_currency_dialog_description_with_name, currency.name),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(MR.strings.delete_category),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(MR.strings.cancel_edit),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        modifier = modifier,
    )
}
