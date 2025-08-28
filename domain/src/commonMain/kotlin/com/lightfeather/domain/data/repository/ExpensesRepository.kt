package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.transaction.Transaction


interface ExpensesRepository: TransactionRepository<Transaction.Expense>