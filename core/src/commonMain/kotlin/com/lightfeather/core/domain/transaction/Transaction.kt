package com.lightfeather.core.domain.transaction

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category

sealed class Transaction(
    val id: Int,
    val name: String,
    val description: String?,
    val amount: Double,
    val timestamp: Long,
    val account: Account
) {

    class Income(
        id: Int, name: String, description: String?,
        amount: Double, timestamp: Long, account: Account, val source: String
    ) : Transaction(id, name, description, amount, timestamp, account)


    class Expense(
        id: Int, name: String, description: String?, categories: List<Category>,
        amount: Double, timestamp: Long, account: Account
    ) : Transaction(id, name, description, amount, timestamp, account)


    class Transfer(
        id: Int, name: String, description: String?,
        amount: Double, timestamp: Long, account: Account,
        val receiverAccount: Account, val fee: Double
    ) : Transaction(id, name, description, amount, timestamp, account)

}