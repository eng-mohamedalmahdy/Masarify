package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import data.dummy.model.DummyDomainModelsProviders

data object TransferDatasourceDummyImp : TransferDatasource {

    override suspend fun createTransaction(transaction: Transaction.Transfer) {

    }

    override suspend fun updateTransaction(transaction: Transaction.Transfer) {

    }

    override suspend fun deleteTransaction(transaction: Transaction.Transfer): Boolean {
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Transfer> {
        return List(0) { DummyDomainModelsProviders.transfer }
    }

    override suspend fun getTransactionById(id: Int): Transaction.Transfer {
        return DummyDomainModelsProviders.transfer
    }

    override suspend fun getMinTransaction(): Transaction.Transfer {
        return DummyDomainModelsProviders.transfer
    }

    override suspend fun getMaxTransaction(): Transaction.Transfer {
        return DummyDomainModelsProviders.transfer
    }

    override suspend fun getAverageTransactionValue(): Double {
        return 50.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Transfer>,
        filter: TransactionFilter
    ): List<Transaction.Transfer> {
        return List(5) { DummyDomainModelsProviders.transfer }
    }
}