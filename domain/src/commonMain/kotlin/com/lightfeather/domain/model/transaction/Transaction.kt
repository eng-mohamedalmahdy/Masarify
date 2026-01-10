package com.lightfeather.domain.model.transaction

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.Category
import kotlinx.serialization.Serializable

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
    data class Income(
        override val id: Int,
        override val name: String,
        override val description: String? = null,
        override val amount: Double,
        override val timestamp: Long,
        override val account: Account,
        val source: Category,
        override val attachments: List<Attachment> = emptyList(),
    ) : Transaction(id, name, description, amount, timestamp, account, attachments) {
        override val accountNewBalance: Double get() = account.balance + amount
        override val accountOldBalance: Double get() = account.balance - amount

        override fun isFeasible(): Boolean = true
    }

    // --- Expense ---
    data class Expense(
        override val id: Int,
        override val name: String,
        override val description: String? = null,
        override val amount: Double,
        override val timestamp: Long,
        override val account: Account,
        val categories: List<Category>,
        override val attachments: List<Attachment> = emptyList(),
    ) : Transaction(id, name, description, amount, timestamp, account, attachments) {
        override val accountNewBalance: Double get() = account.balance - amount
        override val accountOldBalance: Double get() = account.balance + amount

        override fun isFeasible(): Boolean = account.balance >= amount
    }

    // --- Transfer ---
    data class Transfer(
        override val id: Int,
        override val name: String,
        override val description: String? = null,
        override val amount: Double,
        override val timestamp: Long,
        override val account: Account,
        val receiverAccount: Account,
        val fee: Double,
        override val attachments: List<Attachment> = emptyList(),
    ) : Transaction(id, name, description, amount, timestamp, account, attachments) {
        override val accountNewBalance: Double get() = account.balance - amount - fee
        override val accountOldBalance: Double get() = account.balance + amount + fee

        val receiverAccountNewBalance: Double get() = receiverAccount.balance + amount
        val receiverAccountOldBalance: Double get() = receiverAccount.balance - amount

        override fun isFeasible(): Boolean = account.balance >= amount + fee
    }

    companion object
}
