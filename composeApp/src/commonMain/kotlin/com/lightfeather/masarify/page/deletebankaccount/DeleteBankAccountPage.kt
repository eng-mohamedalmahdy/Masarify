package com.lightfeather.masarify.page.deletebankaccount

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.component.molecules.dialog.AppAlertDialog
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeleteBankAccountPage(viewModel: DeleteBankAccountPageViewModel = koinViewModel()) {
    val toBeDeletedBankAccount = viewModel.uiAccount
    DeleteBankAccountPageContent(
        toBeDeletedBankAccount = toBeDeletedBankAccount,
        onConfirm = viewModel::onConfirm,
        onCancel = viewModel::onCancel,
    )
}

@Composable
internal fun DeleteBankAccountPageContent(
    toBeDeletedBankAccount: UiBankAccount,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(MR.strings.delete_bank_account_dialog_title),
        message =
            stringResource(
                MR.strings.delete_bank_account_dialog_description_with_name,
                toBeDeletedBankAccount.name,
            ),
        onConfirm = onConfirm,
        onDismissRequest = onCancel,
    )
}

@Preview
@Composable
fun DeleteBankAccountScreenPreview() {
    AppTheme {
        DeleteBankAccountPageContent(
            UiBankAccount.dummy,
            {},
            {},
        )
    }
}
