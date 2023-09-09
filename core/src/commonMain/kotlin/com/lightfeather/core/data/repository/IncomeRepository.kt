package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.domain.transaction.Transaction

class IncomeRepository(datasource: IncomeDatasource) : TransactionRepository<Transaction.Income>(datasource)