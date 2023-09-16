package di

import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransactionRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.transaction.Transaction
import org.koin.dsl.module

val repositoryModule = module {
    single { ExpensesRepository(get()) }
    single { TransferRepository(get()) }
    single { IncomeRepository(get()) }
}