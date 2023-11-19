package di

import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.data.datasource.CategoryDatasource
import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.masarify.database.BankAccountQueries
import com.lightfeather.masarify.database.CurrencyQueries
import com.lightfeather.masarify.database.Database
import data.dummy.datasourceimp.AccountDatasourceDummyImp
import data.dummy.datasourceimp.CategoryDatasourceDummyImp
import data.dummy.datasourceimp.CurrencyDatasourceDummyImp
import data.dummy.datasourceimp.CurrencyExchangeRateDatasourceDummyImp
import data.dummy.datasourceimp.ExpensesDatasourceDummyImp
import data.dummy.datasourceimp.IncomeDatasourceDummyImp
import data.dummy.datasourceimp.TransferDatasourceDummyImp
import data.local.BankAccountDatasourceLocalImp
import data.local.CurrencyDatasourceLocalImp
import data.local.DatabaseDriverFactory
import data.local.createDatabase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val dummyDataSourceModule = module {
    single<ExpensesDatasource> { ExpensesDatasourceDummyImp }
    single<IncomeDatasource> { IncomeDatasourceDummyImp }
    single<TransferDatasource> { TransferDatasourceDummyImp }
    single<CategoryDatasource> { CategoryDatasourceDummyImp }
//    single<AccountDatasource> { AccountDatasourceDummyImp }
//    single<CurrencyDatasource> { CurrencyDatasourceDummyImp }
    single<CurrencyExchangeRateDatasource> { CurrencyExchangeRateDatasourceDummyImp }
}

val databaseQueriesModule = module {
    single<Database> { createDatabase(get()) }
    single<CurrencyQueries> { get<Database>().currencyQueries }
    single<BankAccountQueries> { get<Database>().bankAccountQueries }

}

val localDatasourceModule = module {

    single<CurrencyDatasource> { CurrencyDatasourceLocalImp(get()) }
    single<AccountDatasource> { BankAccountDatasourceLocalImp(get()) }


//    single<ExpensesDatasource> { ExpensesDatasource(get()) }
//    single<IncomeDatasource> { IncomeDatasource(get()) }
//    single<TransferDatasource> { TransferDatasource(get()) }
//    single<CategoryDatasource> { CategoryDatasource(get()) }
}

expect fun databaseDriverFactoryModule(): Module

fun initKoinAndroid(additionalModules: List<Module>) {
    startKoin {
        modules(additionalModules + getBaseModules())
    }
}

//fun initKoiniOS(appConfig: AppConfig) {
//    initKoin(listOf(module { single { appConfig } }))
//}

internal fun getBaseModules(): List<Module> = listOf(
    useCaseModule,
    repositoryModule,
    dummyDataSourceModule,
    databaseQueriesModule,
    localDatasourceModule
) + databaseDriverFactoryModule()
