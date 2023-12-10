package di

import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.CreateAccount
import com.lightfeather.core.usecase.CreateCategory
import com.lightfeather.core.usecase.CreateCurrency
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.DeleteTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import com.lightfeather.core.usecase.GetAllCategoryIcons
import com.lightfeather.core.usecase.GetAllCurrencies
import com.lightfeather.core.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.core.usecase.GetAllTransactions
import com.lightfeather.core.usecase.UpdateCurrencyExchangeRates
import com.lightfeather.core.usecase.UpdateTransaction
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTransactions(get(), get(),get()) }
    factory { GetAllTransactions(get(), get(), get()) }
    factory { GetAllCategories(get()) }
    factory { GetAllAccounts(get()) }

    factory<CreateTransaction<Transaction.Expense>> (named("expense")){ CreateTransaction(get<ExpensesRepository>(),get(),get()) }
    factory<CreateTransaction<Transaction.Income>>(named("income")) { CreateTransaction(get<IncomeRepository>(),get(),get()) }
    factory<CreateTransaction<Transaction.Transfer>>(named("transfer")) { CreateTransaction(get<TransferRepository>(),get(),get()) }


    factory<DeleteTransaction<Transaction.Expense>> (named("expense")){ DeleteTransaction(get<ExpensesRepository>(),get(),get()) }
    factory<DeleteTransaction<Transaction.Income>>(named("income")) { DeleteTransaction(get<IncomeRepository>(),get(),get()) }
    factory<DeleteTransaction<Transaction.Transfer>>(named("transfer")) { DeleteTransaction(get<TransferRepository>(),get(),get()) }

    factory<UpdateTransaction<Transaction.Expense>> (named("expense")){ UpdateTransaction(get<ExpensesRepository>(),get(),get()) }
    factory<UpdateTransaction<Transaction.Income>>(named("income")) { UpdateTransaction(get<IncomeRepository>(),get(),get()) }
    factory<UpdateTransaction<Transaction.Transfer>>(named("transfer")) { UpdateTransaction(get<TransferRepository>(),get(),get()) }




    factory { GetAllCategoryIcons(get()) }
    factory { CreateCategory(get()) }
    factory { CreateAccount(get()) }
    factory { GetAllCurrencies(get()) }
    factory { GetAllCurrenciesExchangeRates(get()) }
    factory { CreateCurrency(get(),get()) }
    factory { UpdateCurrencyExchangeRates(get()) }
}