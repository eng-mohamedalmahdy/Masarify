package tech.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_currencies
import tech.lightfeather.data.local.database.drivers.SharedDatabase
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyType
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.model.runCatchingDomainResultSuspend
import tech.lightfeather.domain.repository.CurrencyRepository

// DomainResult pattern requires catching all exceptions for proper error handling
@Suppress("TooGenericExceptionCaught")
class CurrencyRepositoryImpl(
    private val database: SharedDatabase,
) : CurrencyRepository {

    override suspend fun createCurrency(currency: Currency): DomainResult<Int> =
        try {
            val result =
                database {
                    val currencyQueries = it.currenciesQueries
                    currencyQueries.transactionWithResult {
                        currencyQueries.insertCurrency(
                            name = currency.name,
                            sign = currency.sign,
                            type = currency.type.name,
                            is_default = if (currency.isDefault) 1L else 0L,
                            resource_key = currency.resourceKey,
                            iso_code = currency.isoCode,
                        )
                        currencyQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }
            Napier.d { "CURRENCY ADDED WITH ID $result CURRENCY: $currency" }
            val newId = result.toInt()
            DomainResult.Success(newId)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating currency"))
        }

    override suspend fun updateCurrency(currency: Currency): DomainResult<Boolean> =
        try {
            val result =
                database {
                    it.currenciesQueries.updateCurrency(
                        name = currency.name,
                        sign = currency.sign,
                        type = currency.type.name,
                        is_default = if (currency.isDefault) 1L else 0L,
                        resource_key = currency.resourceKey,
                        iso_code = currency.isoCode,
                        id = currency.id.toLong(),
                    )
                }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating currency"))
        }

    override suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean> =
        try {
            val result = database { it.currenciesQueries.deleteCurrency(currency.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting currency"))
        }

    override suspend fun getCurrencyById(id: Int): DomainResult<Currency> =
        try {
            val currency =
                database {
                    it.currenciesQueries.selectCurrencyById(id.toLong()).awaitAsOne()
                }
            DomainResult.Success(currency.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currency"))
        }

    override fun getAllCurrencies(): DomainResult<Flow<List<Currency>>> =
        try {
            val currenciesFlow: Flow<List<Currency>> =
                flow {
                    val flow =
                        database { db ->
                            db.currenciesQueries
                                .selectAllCurrencies()
                                .asFlow()
                                .map { query -> query.awaitAsList().map { it.toDomain() } }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(currenciesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting currencies"))
        }

    override suspend fun getUsedCurrencies(): DomainResult<Flow<List<Currency>>> =
        runCatchingDomainResultSuspend {
            database {
                it.currenciesQueries.selectUsedCurrencies().asFlow().map { query ->
                    query.awaitAsList().map { it.toDomain() }
                }
            }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit> =
        try {
            database { it.currenciesQueries.updateRemoteId(remoteId = remoteId, id = localId.toLong()) }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating remote id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> =
        try {
            val id = database { it.currenciesQueries.getLocalIdByRemoteId(remoteId).awaitAsOneOrNull() }
            DomainResult.Success(id?.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting local id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?> =
        try {
            val remoteId =
                database { it.currenciesQueries.getRemoteIdByLocalId(localId.toLong()).awaitAsOneOrNull()?.remote_id }
            DomainResult.Success(remoteId)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting remote id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> =
        try {
            val ids = database { it.currenciesQueries.getUnsyncedCurrencyIds().awaitAsList() }
            DomainResult.Success(ids.map { it.toInt() })
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting unsynced ids"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getLocalIdByResourceKey(resourceKey: String): DomainResult<Int?> =
        try {
            val id = database { it.currenciesQueries.getLocalIdByResourceKey(resourceKey).awaitAsOneOrNull() }
            DomainResult.Success(id?.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting local id by resource key"))
        }

    private fun V_currencies.toDomain(): Currency =
        Currency(
            name = currencyName,
            sign = currencySign,
            id = currencyId.toInt(),
            type = CurrencyType.valueOf(currencyType),
            isDefault = currencyIsDefault == 1L,
            resourceKey = currencyResourceKey,
            isoCode = currencyIsoCode,
        )
}
