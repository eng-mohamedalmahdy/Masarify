package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.AccountRepository
import com.lightfeather.core.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransactionRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import com.lightfeather.core.domain.DomainResult
import com.lightfeather.core.domain.transaction.InvalidTransaction
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>,
    private val accountRepository: AccountRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(transaction: T): DomainResult<Int> =
        with(transaction) {
            if (isFeasible()) {
                if (this is Transaction.Transfer) {
                    val exchangeRate = GetCurrencyExchangeRateById(exchangeRateRepository)(
                        account.currency.id, receiverAccount.currency.id
                    )
                    accountRepository.updateAccount(receiverAccount.copy(balance = copy(amount = amount * exchangeRate.rate).receiverAccountNewBalance))
                }

                accountRepository.updateAccount(account.copy(balance = accountNewBalance))

                transactionRepository.createTransaction(transaction)
            } else DomainResult.Failure(InvalidTransaction("Invalid Transaction double check your balance"))
        }
}


class UpdateTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>,
    private val accountRepository: AccountRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository
) {
    suspend operator fun invoke(oldTransaction: Transaction, newTransaction: T) {
        // Check if the accounts are the same and the modified new transaction is feasible

        val exchangeRate =
            getExchangeRate(
                newTransaction.account.currency.id,
                (newTransaction as? Transaction.Transfer)?.receiverAccount?.currency?.id
                    ?: newTransaction.account.currency.id
            ).rate

        val balanceDifference: Double = when (newTransaction) {
            is Transaction.Income -> newTransaction.amount - oldTransaction.amount
            is Transaction.Expense -> oldTransaction.amount - newTransaction.amount
            is Transaction.Transfer -> {
                val oldFee = (oldTransaction as? Transaction.Transfer)?.fee ?: (newTransaction.fee * 2)
                ((newTransaction.amount - oldTransaction.amount) * exchangeRate) - (newTransaction.fee - oldFee)
            }

            else -> 0.0 // Adjust as needed based on your transaction types
        }
        if (oldTransaction.account.id == newTransaction.account.id && balanceDifference < oldTransaction.account.balance) {

            // Update the account balance
            accountRepository.updateAccount(oldTransaction.account.copy(balance = oldTransaction.account.balance - (balanceDifference / exchangeRate)))

            if (newTransaction is Transaction.Transfer) {
                accountRepository.updateAccount(newTransaction.receiverAccount.copy(balance = newTransaction.receiverAccount.balance + balanceDifference))
            }

            // Update the transaction
            transactionRepository.updateTransaction(newTransaction)

        } else {
            // If the accounts are different or the new transaction is not feasible, perform a full update
            CoroutineScope(Dispatchers.IO).launch {
                if (newTransaction.isFeasible()) {
                    withContext(Dispatchers.IO) {
                        // Delete the old transaction
                        DeleteTransaction(transactionRepository, accountRepository, exchangeRateRepository)(
                            oldTransaction
                        )
                    }
                    withContext(Dispatchers.IO) {
                        // Create the new transaction
                        CreateTransaction(transactionRepository, accountRepository, exchangeRateRepository)(
                            newTransaction
                        )
                    }
                }
            }
        }
    }

    private suspend fun getExchangeRate(fromCurrencyId: Int, toCurrencyId: Int): CurrencyExchangeRate {
        return GetCurrencyExchangeRateById(exchangeRateRepository)(fromCurrencyId, toCurrencyId)
    }
}

class DeleteTransaction<T : Transaction>(
    private val transactionRepository: TransactionRepository<T>,
    private val accountRepository: AccountRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        if (transaction is Transaction.Transfer) {
            val exchangeRate = GetCurrencyExchangeRateById(exchangeRateRepository)(
                transaction.account.currency.id, transaction.receiverAccount.currency.id
            )
            accountRepository.updateAccount(transaction.receiverAccount.copy(balance = transaction.copy(amount = transaction.amount * exchangeRate.rate).receiverAccountOldBalance))
        }
        accountRepository.updateAccount(transaction.account.copy(balance = transaction.accountOldBalance))
        transactionRepository.deleteTransaction(transaction)
    }
}

class GetMaxTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getMaxTransaction()
}

class GetMinTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getMinTransaction()
}

class GetAverageTransactionValue<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getAverageTransactionValue()
}

class GetFilteredTransactions<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(transactions: List<T>, filter: TransactionFilter) =
        transactionRepository.getFilteredTransactions(transactions, filter)
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


class GetAllTransactions(
    private val incomeRepository: IncomeRepository,
    private val expensesRepository: ExpensesRepository,
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(): Flow<List<Transaction>> = combine(
        incomeRepository.getAllTransactions(),
        expensesRepository.getAllTransactions(),
        transferRepository.getAllTransactions()
    ) { incomes: List<Transaction.Income>, expenses: List<Transaction.Expense>, transfers: List<Transaction.Transfer> ->
        incomes + expenses + transfers
    }.map { it.sortedBy { it.timestamp } }
}

class GetTransactionById<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(id: Int) = transactionRepository.getTransactionById(id)
}







