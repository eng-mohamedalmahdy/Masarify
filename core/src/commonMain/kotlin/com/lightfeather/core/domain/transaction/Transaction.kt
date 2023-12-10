package com.lightfeather.core.domain.transaction

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category

sealed class Transaction(
    open val id: Int,
    open val name: String,
    open val description: String?,
    open val amount: Double,
    open val timestamp: Long,
    open val account: Account
) {
    companion object;

    abstract fun isFeasible(): Boolean
    abstract val accountNewBalance: Double
    abstract val accountOldBalance: Double



    data class Income(
        override val id: Int, override val name: String, override val description: String?,
        override val amount: Double, override val timestamp: Long, override val account: Account, val source: Category
    ) : Transaction(id, name, description, amount, timestamp, account) {
        companion object;

        override val accountNewBalance: Double
            get() = account.balance + amount

        override val accountOldBalance: Double
            get() = account.balance - amount

        override fun isFeasible(): Boolean = true

    }

    data class Expense(
        override val id: Int,
        override val name: String,
        override val description: String?,
        val categories: List<Category>,
        override val amount: Double,
        override val timestamp: Long,
        override val account: Account
    ) : Transaction(id, name, description, amount, timestamp, account) {
        companion object;

        override val accountNewBalance: Double
            get() = account.balance - amount

        override val accountOldBalance: Double
            get() = account.balance + amount

        override fun isFeasible(): Boolean = account.balance >= amount
    }

    data class Transfer(
        override val id: Int, override val name: String, override val description: String?,
        override val amount: Double, override val timestamp: Long, override val account: Account,
        val receiverAccount: Account, val fee: Double
    ) : Transaction(id, name, description, amount, timestamp, account) {

        companion object;

        override val accountNewBalance: Double
            get() = account.balance - amount - fee

        override val accountOldBalance: Double
            get() = account.balance + amount + fee

        val receiverAccountNewBalance = receiverAccount.balance + amount
        val receiverAccountOldBalance = receiverAccount.balance - amount


        override fun isFeasible(): Boolean = account.balance >= amount + fee
    }


}