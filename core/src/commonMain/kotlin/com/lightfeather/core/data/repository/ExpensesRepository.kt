package data.repository

import data.datasource.transactions.ExpensesDatasource
import domain.transaction.Transaction

class ExpensesRepository(datasource: ExpensesDatasource) : TransactionRepository<Transaction.Expense>(datasource)