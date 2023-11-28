package di

import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.data.repository.AccountRepository
import com.lightfeather.core.data.repository.CategoryRepository
import com.lightfeather.core.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.core.data.repository.CurrencyRepository
import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransactionRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.transaction.Transaction
import org.koin.dsl.module

val repositoryModule = module {
    single { ExpensesRepository(get<ExpensesDatasource>()) }
    single { TransferRepository(get()) }
    single { IncomeRepository(get()) }
    single { CategoryRepository(get()) }
    single { AccountRepository(get()) }
    single { CurrencyRepository(get()) }
    single { CurrencyExchangeRateRepository(get()) }
}