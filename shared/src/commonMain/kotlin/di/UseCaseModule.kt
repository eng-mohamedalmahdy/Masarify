package di

import com.lightfeather.core.data.repository.ExpensesRepository
import com.lightfeather.core.data.repository.IncomeRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.usecase.CreateAccount
import com.lightfeather.core.usecase.CreateCategory
import com.lightfeather.core.usecase.CreateCurrency
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import com.lightfeather.core.usecase.GetAllCategoryIcons
import com.lightfeather.core.usecase.GetAllCurrencies
import com.lightfeather.core.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.core.usecase.GetAllTransactions
import com.lightfeather.core.usecase.UpdateCurrencyExchangeRates
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetAllTransactions(get(), get(), get()) }
    factory { GetAllCategories(get()) }
    factory { GetAllAccounts(get()) }
    factory { CreateTransaction(get<ExpensesRepository>()) }
    factory { CreateTransaction(get<IncomeRepository>()) }
    factory { CreateTransaction(get<TransferRepository>()) }
    factory { GetAllCategoryIcons(get()) }
    factory { CreateCategory(get()) }
    factory { CreateAccount(get()) }
    factory { GetAllCurrencies(get()) }
    factory { GetAllCurrenciesExchangeRates(get()) }
    factory { CreateCurrency(get()) }
    factory { UpdateCurrencyExchangeRates(get()) }
}