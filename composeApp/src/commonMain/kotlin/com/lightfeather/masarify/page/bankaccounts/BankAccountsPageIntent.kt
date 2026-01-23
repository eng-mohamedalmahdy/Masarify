package com.lightfeather.masarify.page.bankaccounts

import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransaction

internal sealed interface BankAccountsPageIntent {
    data object LoadData : BankAccountsPageIntent

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

    data class SelectAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class UpdateTransaction(
        val transaction: UiTransaction,
    ) : BankAccountsPageIntent

    data class DeleteTransaction(
        val transaction: UiTransaction,
    ) : BankAccountsPageIntent

    data class DuplicateTransaction(
        val transaction: UiTransaction,
    ) : BankAccountsPageIntent

    data object ConfirmDeleteTransaction : BankAccountsPageIntent

    data object CancelDeleteTransaction : BankAccountsPageIntent

    data object ConfirmUpdateTransaction : BankAccountsPageIntent

    data object CancelUpdateTransaction : BankAccountsPageIntent

    // Attachment Operations
    data object PickImages : BankAccountsPageIntent

    data class DeleteAttachment(
        val attachment: UiAttachment,
    ) : BankAccountsPageIntent

    data class LoadAttachments(
        val transactionId: String,
    ) : BankAccountsPageIntent
}
