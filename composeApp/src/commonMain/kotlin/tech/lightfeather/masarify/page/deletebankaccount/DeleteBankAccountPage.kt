package tech.lightfeather.masarify.page.deletebankaccount

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.dialog.AppAlertDialog
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.Account

@Composable
fun DeleteBankAccountPage(account: Account) {
    val viewModel: DeleteBankAccountPageViewModel = koinViewModel { parametersOf(account) }
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
