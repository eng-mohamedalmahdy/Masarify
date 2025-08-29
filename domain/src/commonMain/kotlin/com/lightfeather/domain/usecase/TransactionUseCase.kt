import com.lightfeather.domain.data.repository.AllTransactionsRepository
import com.lightfeather.domain.data.repository.ExpensesRepository
import com.lightfeather.domain.data.repository.IncomeRepository
import com.lightfeather.domain.data.repository.TransactionRepository
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.transaction.Transaction
import com.lightfeather.domain.domain.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow

class CreateTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>
) {
    suspend operator fun invoke(transaction: T): DomainResult<Int> {
        return transactionRepository.createTransaction(transaction)
    }
}

class UpdateTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>
) {
    suspend operator fun invoke(newTransaction: T): DomainResult<Boolean> {
        return transactionRepository.updateTransaction(newTransaction)
    }
}

class DeleteTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>
) {
    suspend operator fun invoke(transaction: Transaction): DomainResult<Boolean> {
        return transactionRepository.deleteTransaction(transaction)
    }
}

class GetTransactionById<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>
) {
    suspend operator fun invoke(id: Int) = transactionRepository.getTransactionById(id)
}

class GetAllTransactions(
   private val allTransactionsRepository: AllTransactionsRepository
) {
    suspend operator fun invoke(): DomainResult<Flow<List<Transaction>>> {
        return allTransactionsRepository.getAllTransactions()
    }
}

// Aggregates — all pushed into repo
class GetMaxTransaction<T : Transaction>(private val repository: TransactionRepository<T>) {
    suspend operator fun invoke() = repository.getMaxTransaction()
}

class GetMinTransaction<T : Transaction>(private val repository: TransactionRepository<T>) {
    suspend operator fun invoke() = repository.getMinTransaction()
}

class GetAverageTransactionValue<T : Transaction>(private val repository: TransactionRepository<T>) {
    suspend operator fun invoke() = repository.getAverageTransactionValue()
}

class GetFilteredTransactions<T : Transaction>(private val repository: TransactionRepository<T>) {
    suspend operator fun invoke(transactions: List<T>, filter: TransactionFilter) =
        repository.getFilteredTransactions(transactions, filter)
}

class GetTotalTransactionsByCategories<T : Transaction>(private val repository: TransactionRepository<T>) {
    suspend operator fun invoke() = repository.getTotalTransactionsOfCategories()
}

class GetTotalExpenseOfCurrency(private val repository: ExpensesRepository) {
    suspend operator fun invoke(currency: Currency) = repository.getTotalTransactionsOfCurrency(currency)
}

class GetTotalIncomeOfCurrency(private val repository: IncomeRepository) {
    suspend operator fun invoke(currency: Currency) = repository.getTotalTransactionsOfCurrency(currency)
}
