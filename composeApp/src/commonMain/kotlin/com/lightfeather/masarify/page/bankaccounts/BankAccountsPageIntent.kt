package com.lightfeather.masarify.page.bankaccounts

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency

internal sealed interface BankAccountsPageIntent {
    data object LoadData : BankAccountsPageIntent

    sealed interface NavigationIntent : BankAccountsPageIntent {
        data object AddBankAccount : NavigationIntent

        data class UpdateBankAccount(
            val account: UiBankAccount,
        ) : NavigationIntent

        data class SelectAccount(
            val account: UiBankAccount,
        ) : NavigationIntent
    }

    data class DeleteBankAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class CreateTransactionInAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class TransferFromAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class SelectCurrency(
        val currency: UiCurrency?,
    ) : BankAccountsPageIntent

    data object ClearNavigation : BankAccountsPageIntent
}
