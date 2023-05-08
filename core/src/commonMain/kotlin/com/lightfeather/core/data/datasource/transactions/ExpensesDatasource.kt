package data.datasource.transactions

import domain.transaction.Transaction

interface ExpensesDatasource : TransactionDatasource<Transaction.Expense>