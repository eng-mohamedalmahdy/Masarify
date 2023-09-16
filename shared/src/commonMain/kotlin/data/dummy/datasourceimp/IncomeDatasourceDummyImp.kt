package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import data.dummy.model.DummyDomainModelsProviders

data object IncomeDatasourceDummyImp : IncomeDatasource {
    override suspend fun createTransaction(transaction: Transaction.Income) {

    }

    override suspend fun updateTransaction(transaction: Transaction.Income) {

    }

    override suspend fun deleteTransaction(transaction: Transaction.Income): Boolean {
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Income> {
        return List(10) { DummyDomainModelsProviders.income }
    }

    override suspend fun getTransactionById(id: Int): Transaction.Income {
        return DummyDomainModelsProviders.income
    }

    override suspend fun getMinTransaction(): Transaction.Income {
        return DummyDomainModelsProviders.income
    }

    override suspend fun getMaxTransaction(): Transaction.Income {
        return DummyDomainModelsProviders.income
    }

    override suspend fun getAverageTransactionValue(): Double {
        return 100.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Income>,
        filter: TransactionFilter
    ): List<Transaction.Income> {
        return List(5) { DummyDomainModelsProviders.income }
    }

}