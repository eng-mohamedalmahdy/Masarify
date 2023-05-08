package data.repository

import data.datasource.transactions.TransferDatasource
import domain.Account
import domain.transaction.Transaction

class TransferRepository(private val datasource: TransferDatasource) :
    TransactionRepository<Transaction.Transfer>(datasource) {
    fun transfer(from: Account, to: Account, amount: Double, fee: Double = 0.0) = datasource.transfer(from, to, amount, fee)
}