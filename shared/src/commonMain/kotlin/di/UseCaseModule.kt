package di

import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import com.lightfeather.core.usecase.GetAllTransactions
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTransactions(get(), get(), get()) }
    factory { GetAllCategories(get()) }
    factory { GetAllAccounts(get()) }
    factory { CreateTransaction(get<ExpensesRepository>()) }
    factory { CreateTransaction(get<IncomeRepository>()) }
    factory { CreateTransaction(get<TransferRepository>()) }
}