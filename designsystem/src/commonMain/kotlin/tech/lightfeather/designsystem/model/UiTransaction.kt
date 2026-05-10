package tech.lightfeather.designsystem.model

import tech.lightfeather.designsystem.util.now
import kotlinx.datetime.LocalDateTime

/**
 * UI representation of a transaction for display in lists and details
 * Supports three transaction types: Expense, Income, and Transfer
 */
data class UiTransaction(
    val id: String,
    val type: UiTransactionType,
    val name: String,
    val amount: String,
    val dateTime: LocalDateTime,
    val description: String,
    val account: UiBankAccount,
    val category: UiCategory,
    val hasAttachment: Boolean,
    val balanceBefore: String = "",
    val balanceAfter: String = "",
    // Transfer-specific fields
    val receiverAccount: UiBankAccount? = null,
    val transferFee: String? = null,
) {
    /**
     * Check if this is a transfer transaction
     */
    val isTransfer: Boolean get() = type == UiTransactionType.TRANSFER

    /**
     * Get display label for the transaction based on type
     */
    val typeLabel: String
        get() =
            when (type) {
                UiTransactionType.EXPENSE -> "Expense"
                UiTransactionType.INCOME -> "Income"
                UiTransactionType.TRANSFER -> "Transfer"
            }

    companion object {
        val dummy =
            UiTransaction(
                id = "1",
                name = "Coffee Purchase",
                amount = "100",
                type = UiTransactionType.EXPENSE,
                dateTime = LocalDateTime.now(),
                description = "Coffee Shop Purchase",
                account = UiBankAccount.dummy,
                category = UiCategory.dummy,
                hasAttachment = false,
            )

        val dummyTransfer =
            UiTransaction(
                id = "2",
                name = "Savings Transfer",
                amount = "500",
                type = UiTransactionType.TRANSFER,
                dateTime = LocalDateTime.now(),
                description = "Transfer to savings account",
                account = UiBankAccount.dummy,
                category = UiCategory.dummy,
                hasAttachment = false,
                receiverAccount = UiBankAccount.dummy.copy(id = "2", name = "Savings Account"),
                transferFee = "5",
            )
    }
}
