package data.dummy.datasourceimp

import androidx.compose.ui.input.key.Key.Companion.D
import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import data.dummy.model.DummyDomainModelsProviders

data object ExpensesDatasourceDummyImp : ExpensesDatasource {
    override suspend fun createTransaction(transaction: Transaction.Expense) {

    }

    override suspend fun updateTransaction(transaction: Transaction.Expense) {

    }

    override suspend fun deleteTransaction(transaction: Transaction.Expense): Boolean {
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Expense> {
        return List(10) { DummyDomainModelsProviders.expense }
    }

    override suspend fun getTransactionById(id: Int): Transaction.Expense {
        return DummyDomainModelsProviders.expense
    }

    override suspend fun getMinTransaction(): Transaction.Expense {
        return DummyDomainModelsProviders.expense
    }

    override suspend fun getMaxTransaction(): Transaction.Expense {
        return DummyDomainModelsProviders.expense
    }

    override suspend fun getAverageTransactionValue(): Double {
        return 50.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Expense>,
        filter: TransactionFilter
    ): List<Transaction.Expense> {
        return List(5){DummyDomainModelsProviders.expense}
    }

}