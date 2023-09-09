package com.lightfeather.core.data.datasource.transactions

import com.lightfeather.core.data.datasource.transactions.TransactionDatasource
import com.lightfeather.core.domain.transaction.Transaction

interface IncomeDatasource : TransactionDatasource<Transaction.Income>