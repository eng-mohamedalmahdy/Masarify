package data.local

import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.masarify.database.CurrenciesQueries

class CurrencyDatasourceLocalImp(private val currencyQueries: CurrenciesQueries) : CurrencyDatasource {
    override suspend fun createCurrency(currency: Currency): Int {
        currencyQueries.insertCurrency(currency.name, currency.sign)
        return (currencyQueries.selectLastInsertedRowId().executeAsOneOrNull() ?: -1L).toInt()
    }

    override suspend fun getAllCurrencies(): List<Currency> {
        return currencyQueries.selectAllCurrencies().executeAsList()
            .map { with(it) { Currency(id.toInt(), name, sign) } }
    }

    override suspend fun updateCurrency(currency: Currency) {
        currencyQueries.updateCurreny(currency.name, currency.sign, currency.id.toLong())
    }

    override suspend fun deleteCurrency(currency: Currency): Boolean {
        currencyQueries.deleteCurrency(currency.id.toLong())
        return true
    }


    override suspend fun getCurrencyById(id: Int): Currency? {
        return currencyQueries.selectCurrencyById(id.toLong()).executeAsOneOrNull()?.let { with(it){Currency(id,name,sign)} }
    }
}