package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.transaction.Transaction

class TransferRepository(private val datasource: TransferDatasource) :
    TransactionRepository<Transaction.Transfer>(datasource) {
    fun transfer(from: Account, to: Account, amount: Double, fee: Double = 0.0) = datasource.transfer(from, to, amount, fee)
}