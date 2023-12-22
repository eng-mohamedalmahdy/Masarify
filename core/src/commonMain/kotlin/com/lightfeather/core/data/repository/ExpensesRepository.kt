package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.domain.transaction.Transaction

class ExpensesRepository(private val datasource: ExpensesDatasource) : TransactionRepository<Transaction.Expense>(datasource)