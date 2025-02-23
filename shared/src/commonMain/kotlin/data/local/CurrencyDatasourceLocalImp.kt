package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.masarify.database.CurrenciesQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class CurrencyDatasourceLocalImp(private val currencyQueries: CurrenciesQueries) : CurrencyDatasource {
    override suspend fun createCurrency(currency: Currency): Int {
        currencyQueries.insertCurrency(currency.name, currency.sign)
        return (currencyQueries.selectLastInsertedRowId().executeAsOneOrNull() ?: -1L).toInt()
    }

    override fun getAllCurrencies(): Flow<List<Currency>> {
        return currencyQueries.selectAllCurrencies { id, name, sign ->
            Currency(id.toInt(), name, sign)
        }.asFlow().mapToList(Dispatchers.IO)

    }

    override suspend fun updateCurrency(currency: Currency) {
        currencyQueries.updateCurreny(currency.name, currency.sign, currency.id.toLong())
    }

    override suspend fun deleteCurrency(currency: Currency): Boolean {
        currencyQueries.deleteCurrency(currency.id.toLong())
        return true
    }


    override suspend fun getCurrencyById(id: Int): Currency? {
        return currencyQueries.selectCurrencyById(id.toLong()).executeAsOneOrNull()
            ?.let { with(it) { Currency(id, name, sign) } }
    }
}