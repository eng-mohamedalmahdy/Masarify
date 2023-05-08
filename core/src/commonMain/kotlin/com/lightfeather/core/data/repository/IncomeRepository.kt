package data.repository

import data.datasource.transactions.IncomeDatasource
import domain.transaction.Transaction

class IncomeRepository(datasource: IncomeDatasource) : TransactionRepository<Transaction.Income>(datasource)