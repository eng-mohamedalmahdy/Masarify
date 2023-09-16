package di

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.GetAllTransactions
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTransactions(get(), get(), get()) }
}