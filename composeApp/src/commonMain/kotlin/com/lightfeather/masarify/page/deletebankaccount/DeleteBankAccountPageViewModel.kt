package com.lightfeather.masarify.page.deletebankaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.usecase.DeleteAccount
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.navigation.Navigator
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
