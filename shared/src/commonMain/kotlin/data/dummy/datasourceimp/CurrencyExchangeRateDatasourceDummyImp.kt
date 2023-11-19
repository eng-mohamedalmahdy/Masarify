package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import data.dummy.model.DummyDomainModelsProviders

object CurrencyExchangeRateDatasourceDummyImp : CurrencyExchangeRateDatasource {
    override suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrencyExchangeRates(rate: List<List<CurrencyExchangeRate>>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>> {
        return List(5) { x ->
            List(5) { y ->
                CurrencyExchangeRate(
                    DummyDomainModelsProviders.currency.copy(id = x),
                    DummyDomainModelsProviders.currency.copy(id = y),
                    ((x+1) * (y+1)).toDouble()
                )
            }
        }
    }

    override suspend fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate {
        TODO("Not yet implemented")
    }

    override suspend fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate> {
        TODO("Not yet implemented")
    }
}