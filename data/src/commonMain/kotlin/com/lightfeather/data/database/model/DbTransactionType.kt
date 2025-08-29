package com.lightfeather.data.database.model

enum class DbTransactionType(val dbValue: String) {
    Expense("EXPENSE"),
    Income("INCOME"),
    Transfer("TRANSFER")
}