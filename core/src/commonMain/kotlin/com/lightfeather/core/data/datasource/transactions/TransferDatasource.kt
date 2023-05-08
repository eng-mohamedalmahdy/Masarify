package data.datasource.transactions

import domain.Account
import domain.transaction.Transaction

interface TransferDatasource : TransactionDatasource<Transaction.Transfer> {
    fun transfer(from: Account, to: Account, amount: Double, fee: Double = 0.0)
}