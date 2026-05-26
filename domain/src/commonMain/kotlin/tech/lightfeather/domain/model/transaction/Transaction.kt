package tech.lightfeather.domain.model.transaction

import kotlinx.serialization.Serializable
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.Category

@Serializable
sealed class Transaction(
    open val id: Int,
    open val name: String,
    open val description: String?,
    open val amount: Double,
    open val timestamp: Long,
    open val account: Account,
    open val attachments: List<Attachment> = emptyList(),
) {
    abstract fun isFeasible(): Boolean

    abstract val accountNewBalance: Double
    abstract val accountOldBalance: Double

    // --- Income ---
    @Serializable
    data class Income(
        val incomeId: Int,
        val incomeName: String,
        val incomeDescription: String? = null,
        val incomeAmount: Double,
        val incomeTimestamp: Long,
        val incomeAccount: Account,
        val incomeAttachments: List<Attachment> = emptyList(),
        val source: Category,
    ) : Transaction(
            incomeId,
            incomeName,
            incomeDescription,
            incomeAmount,
            incomeTimestamp,
            incomeAccount,
            incomeAttachments,
        ) {
        override val accountNewBalance: Double get() = account.balance + amount
        override val accountOldBalance: Double get() = account.balance - amount

        override fun isFeasible(): Boolean = true
    }

    // --- Expense ---
    @Serializable
    data class Expense(
        val expenseId: Int,
        val expenseName: String,
        val expenseDescription: String? = null,
        val expenseAmount: Double,
        val expenseTimestamp: Long,
        val expenseAccount: Account,
        val categories: List<Category>,
        val expenseAttachments: List<Attachment> = emptyList(),
    ) : Transaction(
            expenseId,
            expenseName,
            expenseDescription,
            expenseAmount,
            expenseTimestamp,
            expenseAccount,
            expenseAttachments,
        ) {
        override val accountNewBalance: Double get() = account.balance - amount
        override val accountOldBalance: Double get() = account.balance + amount

        override fun isFeasible(): Boolean = account.balance >= amount
    }

    // --- Transfer ---
    @Serializable
    data class Transfer(
        val transferId: Int,
        val transferName: String,
        val transferDescription: String? = null,
        val transferAmount: Double,
        val transferTimestamp: Long,
        val transferAccount: Account,
        val receiverAccount: Account,
        val fee: Double,
        val transferAttachments: List<Attachment> = emptyList(),
    ) : Transaction(
            transferId,
            transferName,
            transferDescription,
            transferAmount,
            transferTimestamp,
            transferAccount,
            transferAttachments,
        ) {
        override val accountNewBalance: Double get() = account.balance - amount - fee
        override val accountOldBalance: Double get() = account.balance + amount + fee

        val receiverAccountNewBalance: Double get() = receiverAccount.balance + amount
        val receiverAccountOldBalance: Double get() = receiverAccount.balance - amount

        override fun isFeasible(): Boolean = account.balance >= amount + fee
    }

    companion object
}
