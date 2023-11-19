package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.domain.Currency
import data.dummy.model.DummyDomainModelsProviders

object CurrencyDatasourceDummyImp : CurrencyDatasource {
    override suspend fun createCurrency(currency: Currency): Int {
        return 10
    }

    override suspend fun updateCurrency(currency: Currency) {

    }

    override suspend fun deleteCurrency(currency: Currency): Boolean {
        return true
    }

    override suspend fun getAllCurrencies(): List<Currency> {
        return List(5) { DummyDomainModelsProviders.currency.copy(id = it, name = DummyDomainModelsProviders.currency.name + "$it") }
    }

    override suspend fun getCurrencyById(id: Int): Currency {
        return DummyDomainModelsProviders.currency.copy(id = id)
    }
}