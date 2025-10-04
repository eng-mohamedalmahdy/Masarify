package com.lightfeather.masarify.page.currencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun AddEditCurrencyDialog(
    currency: UiCurrency?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, symbol: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember(currency) { mutableStateOf(currency?.name ?: "") }
    var symbol by remember(currency) { mutableStateOf(currency?.symbol ?: "") }

    val isEdit = currency != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text =
                    stringResource(
                        if (isEdit) MR.strings.edit_currency_dialog_title else MR.strings.add_currency_dialog_title,
                    ),
            )
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(MR.strings.currency_name),
                    placeholder = stringResource(MR.strings.enter_currency_name),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(AppTheme.dimens.default))

                TextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = stringResource(MR.strings.currency_symbol),
                    placeholder = stringResource(MR.strings.enter_currency_symbol),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name.trim(), symbol.trim())
                },
            ) {
                Text(
                    text = stringResource(MR.strings.save),
                    color = MaterialTheme.colorScheme.primary,
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
