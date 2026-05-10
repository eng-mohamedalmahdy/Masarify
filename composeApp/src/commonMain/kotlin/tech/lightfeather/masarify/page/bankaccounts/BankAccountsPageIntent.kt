package tech.lightfeather.masarify.page.bankaccounts

import tech.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiTransactionDetails

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
        val transaction: UiTransactionDetails,
    ) : BankAccountsPageIntent

    data class DeleteTransaction(
        val transaction: UiTransactionDetails,
    ) : BankAccountsPageIntent

    data class DuplicateTransaction(
        val transaction: UiTransactionDetails,
    ) : BankAccountsPageIntent

    data object ConfirmDeleteTransaction : BankAccountsPageIntent

    data object CancelDeleteTransaction : BankAccountsPageIntent

    data class ConfirmUpdateTransaction(
        val transactionDate: UiTransactionData,
    ) : BankAccountsPageIntent

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
