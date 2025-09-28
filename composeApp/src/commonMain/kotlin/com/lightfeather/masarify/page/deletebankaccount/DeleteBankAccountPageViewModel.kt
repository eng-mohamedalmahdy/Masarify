package com.lightfeather.masarify.page.deletebankaccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.usecase.DeleteAccount
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.mappers.toUiBankAccount
import com.lightfeather.masarify.navigation.NavTypeProvider
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import kotlinx.coroutines.launch

class DeleteBankAccountPageViewModel(
    private val navigator: Navigator,
    private val deleteBankAccount: DeleteAccount,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val toBeDeletedAccount =
        savedStateHandle
            .toRoute<DeleteAccountRoute>(
                typeMap = mapOf(NavTypeProvider.provideMapEntry<Account>()),
            ).account

    val uiAccount = toBeDeletedAccount.toUiBankAccount()

    fun onConfirm() {
        viewModelScope.launch {
            deleteBankAccount(toBeDeletedAccount).foldResult(
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
