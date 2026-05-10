package tech.lightfeather.designsystem.model

import tech.lightfeather.designsystem.util.now
import kotlinx.datetime.LocalDateTime

data class UiTransactionDetails(
    val id: String,
    val name: String,
    val type: UiTransactionType,
    val amount: String,
    val description: String,
    val dateTime: LocalDateTime,
    val categories: List<UiCategory>,
    val attachments: List<UiAttachment>,
    val account: UiBankAccount,
    val receiverAccount: UiBankAccount?,
    val transferFee: String,
) {
    val typeLabel: String
        get() =
            when (type) {
                UiTransactionType.EXPENSE -> "Expense"
                UiTransactionType.INCOME -> "Income"
                UiTransactionType.TRANSFER -> "Transfer"
            }

    companion object {
        val dummyTransfer =
            UiTransactionDetails(
                id = "2",
                name = "Savings Transfer",
                amount = "500",
                type = UiTransactionType.TRANSFER,
                dateTime = LocalDateTime.now(),
                description = "Transfer to savings account",
                categories = emptyList(),
                attachments = emptyList(),
                account = UiBankAccount.dummy,
                receiverAccount = UiBankAccount.dummy,
                transferFee = "5",
            )
    }
}
