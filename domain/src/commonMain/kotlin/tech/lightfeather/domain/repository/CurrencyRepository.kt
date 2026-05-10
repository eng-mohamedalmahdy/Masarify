package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.DomainResult

interface CurrencyRepository {
    suspend fun createCurrency(currency: Currency): DomainResult<Int>

    suspend fun updateCurrency(currency: Currency): DomainResult<Boolean>

    suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean>

    suspend fun getCurrencyById(id: Int): DomainResult<Currency>

    fun getAllCurrencies(): DomainResult<Flow<List<Currency>>>

    suspend fun getUsedCurrencies(): DomainResult<Flow<List<Currency>>>

    suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit>

    suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?>

    suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?>

    suspend fun getUnsyncedIds(): DomainResult<List<Int>>

    suspend fun getLocalIdByResourceKey(resourceKey: String): DomainResult<Int?>
}
