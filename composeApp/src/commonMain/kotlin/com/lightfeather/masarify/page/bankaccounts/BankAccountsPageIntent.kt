package com.lightfeather.masarify.page.bankaccounts

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency

internal sealed interface BankAccountsPageIntent {
    data class UpdateBankAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class DeleteBankAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class CreateTransactionInAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class AddBankAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class TransferFromAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class SelectCurrency(
        val currency: UiCurrency?,
    ) : BankAccountsPageIntent

    data class SelectAccount(
        val account: UiBankAccount?,
    ) : BankAccountsPageIntent
}
