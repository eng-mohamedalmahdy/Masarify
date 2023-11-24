package di

import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.data.datasource.CategoryDatasource
import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.masarify.database.BankAccountsQueries
import com.lightfeather.masarify.database.CategoriesQueries
import com.lightfeather.masarify.database.CurrenciesQueries
import com.lightfeather.masarify.database.CurrencyExchangeRateQueries
import com.lightfeather.masarify.database.Database
import data.dummy.datasourceimp.CategoryDatasourceDummyImp
import data.dummy.datasourceimp.ExpensesDatasourceDummyImp
import data.dummy.datasourceimp.IncomeDatasourceDummyImp
import data.dummy.datasourceimp.TransferDatasourceDummyImp
import data.local.BankAccountDatasourceLocalImp
import data.local.CategoryDatasourceReleaseImp
import data.local.CurrencyDatasourceLocalImp
import data.local.CurrencyExchangeRateDatasourceLocalImp
import data.local.createDatabase
import data.remote.httpClient
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val dummyDataSourceModule = module {
    single<ExpensesDatasource> { ExpensesDatasourceDummyImp }
    single<IncomeDatasource> { IncomeDatasourceDummyImp }
    single<TransferDatasource> { TransferDatasourceDummyImp }

//    single<CategoryDatasource> { CategoryDatasourceDummyImp }

//    single<AccountDatasource> { AccountDatasourceDummyImp }
//    single<CurrencyDatasource> { CurrencyDatasourceDummyImp }
//    single<CurrencyExchangeRateDatasource> { CurrencyExchangeRateDatasourceDummyImp }
}

val databaseQueriesModule = module {
    single<Database> { createDatabase(get()) }
    single<CurrenciesQueries> { get<Database>().currenciesQueries }
    single<BankAccountsQueries> { get<Database>().bankAccountsQueries }
    single<CurrencyExchangeRateQueries> { get<Database>().currencyExchangeRateQueries }
    single<CategoriesQueries> { get<Database>().categoriesQueries }
}

val httpClientModule = module {
    single<HttpClient> { httpClient  }
}
val localDatasourceModule = module {

    single<CurrencyDatasource> { CurrencyDatasourceLocalImp(get()) }
    single<AccountDatasource> { BankAccountDatasourceLocalImp(get()) }
    single<CurrencyExchangeRateDatasource> { CurrencyExchangeRateDatasourceLocalImp(get()) }

    single<CategoryDatasource> { CategoryDatasourceReleaseImp(get(),get()) }

//    single<TransferDatasource> { TransferDatasource(get()) }
//    single<ExpensesDatasource> { ExpensesDatasource(get()) }
//    single<IncomeDatasource> { IncomeDatasource(get()) }
}

expect fun databaseDriverFactoryModule(): Module

fun initKoinAndroid(additionalModules: List<Module>) {
    startKoin {
        modules(additionalModules + getBaseModules())
    }
}


internal fun getBaseModules(): List<Module> = listOf(
    useCaseModule,
    repositoryModule,
    dummyDataSourceModule,
    databaseQueriesModule,
    localDatasourceModule,
    httpClientModule
) + databaseDriverFactoryModule()
