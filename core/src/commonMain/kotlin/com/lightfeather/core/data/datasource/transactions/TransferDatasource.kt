package com.lightfeather.core.data.datasource.transactions

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.transaction.Transaction

interface TransferDatasource : TransactionDatasource<Transaction.Transfer> {
  suspend  fun transfer(from: Account, to: Account, amount: Double, fee: Double = 0.0)
}