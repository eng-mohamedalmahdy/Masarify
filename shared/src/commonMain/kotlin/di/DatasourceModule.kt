package di

import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import data.dummy.datasourceimp.ExpensesDatasourceDummyImp
import data.dummy.datasourceimp.IncomeDatasourceDummyImp
import data.dummy.datasourceimp.TransferDatasourceDummyImp
import org.koin.dsl.module

val dataSourceModule = module {
    single<ExpensesDatasource> { ExpensesDatasourceDummyImp }
    single<IncomeDatasource> { IncomeDatasourceDummyImp }
    single<TransferDatasource> { TransferDatasourceDummyImp }
}