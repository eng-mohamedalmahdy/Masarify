package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.AccountSnapshot
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.FinancialSession
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.repository.FinancialSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.Financial_sessions
import lightfeather.masarify.database.GetSnapshotsForSession

class FinancialSessionRepositoryImpl(
    private val database: SharedDatabase,
) : FinancialSessionRepository {
    @Suppress("TooGenericExceptionCaught")
    override suspend fun createSession(session: FinancialSession): DomainResult<Int> =
        try {
            val sessionId =
                database { db ->
                    val queries = db.financialSessionsQueries
                    queries.transactionWithResult {
                        queries.insertSession(
                            timestamp = session.timestamp,
                            name = session.name,
                        )
                        val newId = queries.selectLastInsertedRowId().awaitAsOne()
                        session.accountSnapshots.forEach { snapshot ->
                            queries.insertSnapshot(
                                session_id = newId,
                                account_id = snapshot.account.id.toLong(),
                                starting_balance = snapshot.startingBalance,
                            )
                        }
                        newId
                    }
                }
            DomainResult.Success(sessionId.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating session"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateSession(session: FinancialSession): DomainResult<Boolean> =
        try {
            database { db ->
                val queries = db.financialSessionsQueries
                queries.transactionWithResult {
                    queries.updateSession(name = session.name, id = session.id.toLong())
                    queries.deleteSnapshotsForSession(session_id = session.id.toLong())
                    session.accountSnapshots.forEach { snapshot ->
                        queries.insertSnapshot(
                            session_id = session.id.toLong(),
                            account_id = snapshot.account.id.toLong(),
                            starting_balance = snapshot.startingBalance,
                        )
                    }
                }
            }
            DomainResult.Success(true)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating session"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteSession(sessionId: Int): DomainResult<Boolean> =
        try {
            database { db ->
                db.financialSessionsQueries.deleteSession(id = sessionId.toLong())
            }
            DomainResult.Success(true)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting session"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getAllSessions(): DomainResult<Flow<List<FinancialSession>>> =
        try {
            val sessionsFlow: Flow<List<FinancialSession>> =
                flow {
                    val flow =
                        database { db ->
                            db.financialSessionsQueries
                                .getAllSessions()
                                .asFlow()
                                .map { query ->
                                    val sessions = query.awaitAsList()
                                    sessions.map { session ->
                                        val snapshots =
                                            db.financialSessionsQueries
                                                .getSnapshotsForSession(session_id = session.id)
                                                .awaitAsList()
                                        session.toDomain(snapshots)
                                    }
                                }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(sessionsFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting sessions"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getSessionById(id: Int): DomainResult<FinancialSession> =
        try {
            val result =
                database { db ->
                    val session =
                        db.financialSessionsQueries
                            .getSessionById(id = id.toLong())
                            .awaitAsOneOrNull()
                            ?: throw IllegalArgumentException("Session not found: $id")
                    val snapshots =
                        db.financialSessionsQueries
                            .getSnapshotsForSession(session_id = session.id)
                            .awaitAsList()
                    session.toDomain(snapshots)
                }
            DomainResult.Success(result)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting session"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateRemoteId(localId: Int, remoteId: Long): DomainResult<Unit> =
        try {
            database { it.financialSessionsQueries.updateRemoteId(remoteId = remoteId, id = localId.toLong()) }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating remote id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> =
        try {
            val id = database { it.financialSessionsQueries.getLocalIdByRemoteId(remoteId).awaitAsOneOrNull() }
            DomainResult.Success(id?.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting local id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> =
        try {
            val ids = database { it.financialSessionsQueries.getUnsyncedSessionIds().awaitAsList() }
            DomainResult.Success(ids.map { it.toInt() })
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting unsynced ids"))
        }

    private fun Financial_sessions.toDomain(snapshots: List<GetSnapshotsForSession>): FinancialSession =
        FinancialSession(
            id = id.toInt(),
            timestamp = timestamp,
            name = name,
            accountSnapshots = snapshots.map { it.toSnapshot() },
        )

    private fun GetSnapshotsForSession.toSnapshot(): AccountSnapshot =
        AccountSnapshot(
            account =
                Account(
                    id = account_id.toInt(),
                    name = account_name,
                    description = null,
                    balance = starting_balance,
                    color = account_color,
                    logo = account_logo,
                    currency =
                        Currency(
                            id = currency_id.toInt(),
                            name = currency_name,
                            sign = currency_symbol,
                        ),
                ),
            startingBalance = starting_balance,
        )
}
