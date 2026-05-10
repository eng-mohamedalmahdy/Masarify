package tech.lightfeather.masarify.page.deletebankaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.usecase.DeleteAccount
import tech.lightfeather.masarify.mappers.toUiBankAccount
import tech.lightfeather.masarify.navigation.Navigator
import kotlinx.coroutines.launch

class DeleteBankAccountPageViewModel(
    private val navigator: Navigator,
    private val deleteBankAccount: DeleteAccount,
    toBeDeletedAccount: Account,
) : ViewModel() {
    val uiAccount = toBeDeletedAccount.toUiBankAccount()
    private val accountToDelete = toBeDeletedAccount

    fun onConfirm() {
        viewModelScope.launch {
            deleteBankAccount(accountToDelete).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.account_delete_success)
                    navigator.navigateUp()
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.account_delete_failure)
                    navigator.navigateUp()
                },
            )
        }
    }

    fun onCancel() {
        navigator.navigateUp()
    }
}
