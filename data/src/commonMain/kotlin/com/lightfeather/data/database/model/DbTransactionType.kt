package com.lightfeather.data.database.model

import com.lightfeather.domain.domain.transaction.Transaction
import kotlin.reflect.KClass

enum class DbTransactionType(val dbValue: String) {
    Expense("EXPENSE"),
    Income("INCOME"),
    Transfer("TRANSFER")
}

fun String.toDbTransactionType(): DbTransactionType {
    return when (this) {
        "EXPENSE" -> DbTransactionType.Expense
        "INCOME" -> DbTransactionType.Income
        "TRANSFER" -> DbTransactionType.Transfer
        else -> throw IllegalArgumentException("Unknown transaction type")
    }
}

fun <T : Transaction> T.toDbTransactionType(): DbTransactionType {
    return when (this) {
        is Transaction.Expense -> DbTransactionType.Expense
        is Transaction.Income -> DbTransactionType.Income
        is Transaction.Transfer -> DbTransactionType.Transfer
    }
}

fun <T : Transaction> KClass<T>.toDbTransactionType(): DbTransactionType {
    return when (this) {
        Transaction.Expense::class -> DbTransactionType.Expense
        Transaction.Income::class -> DbTransactionType.Income
        Transaction.Transfer::class -> DbTransactionType.Transfer
        else -> throw IllegalArgumentException("Unknown transaction type")
    }
}
