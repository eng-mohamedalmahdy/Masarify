package tech.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_bank_names
import tech.lightfeather.data.local.database.drivers.SharedDatabase
import tech.lightfeather.domain.model.BankName
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.repository.BankNameRepository

// DomainResult pattern requires catching all exceptions for proper error handling
@Suppress("TooGenericExceptionCaught")
class BankNameRepositoryImpl(
    private val database: SharedDatabase,
) : BankNameRepository {
    override suspend fun createBankName(bankName: BankName): DomainResult<Int> =
        try {
            val result =
                database {
                    val bankNameQueries = it.bankNamesQueries
                    bankNameQueries.transactionWithResult {
                        bankNameQueries.insertBankName(
                            name = bankName.name,
                            resource_key = bankName.resourceKey,
                            logo_url = bankName.logoUrl,
                            is_default = if (bankName.isDefault) 1L else 0L,
                        )
                        bankNameQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }
            Napier.d { "BANK NAME ADDED WITH ID $result: $bankName" }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating bank name"))
        }

    override suspend fun updateBankName(bankName: BankName): DomainResult<Boolean> =
        try {
            val result =
                database {
                    it.bankNamesQueries.updateBankName(
                        name = bankName.name,
                        resource_key = bankName.resourceKey,
                        logo_url = bankName.logoUrl,
                        is_default = if (bankName.isDefault) 1L else 0L,
                        id = bankName.id.toLong(),
                    )
                }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating bank name"))
        }

    override suspend fun deleteBankName(bankName: BankName): DomainResult<Boolean> =
        try {
            val result = database { it.bankNamesQueries.deleteBankName(bankName.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting bank name"))
        }

    override suspend fun getBankNameById(id: Int): DomainResult<BankName> =
        try {
            val bankName =
                database {
                    it.bankNamesQueries.selectBankNameById(id.toLong()).awaitAsOne()
                }
            DomainResult.Success(bankName.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting bank name"))
        }

    override fun getAllBankNames(): DomainResult<Flow<List<BankName>>> =
        try {
            val bankNamesFlow: Flow<List<BankName>> =
                flow {
                    val flow =
                        database { db ->
                            db.bankNamesQueries
                                .selectAllBankNames()
                                .asFlow()
                                .map { query -> query.awaitAsList().map { it.toDomain() } }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(bankNamesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting bank names"))
        }

    private fun V_bank_names.toDomain(): BankName =
        BankName(
            id = bankNameId.toInt(),
            name = bankName,
            resourceKey = bankNameResourceKey,
            logoUrl = bankNameLogoUrl,
            isDefault = (bankNameIsDefault ?: 0L) == 1L,
        )
}
