package com.lightfeather.data.repository

import com.lightfeather.data.local.database.DbFileAccessor
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.repository.BackupRepository

class BackupRepositoryImpl(
    private val sharedDatabase: SharedDatabase,
    private val dbFileAccessor: DbFileAccessor,
) : BackupRepository {
    override suspend fun exportDatabase(): DomainResult<ByteArray> =
        runCatchingDomainResultSuspend {
            dbFileAccessor.exportDatabaseBytes()
                ?: error("Export failed: database file not accessible")
        }

    override suspend fun importDatabaseClean(data: ByteArray): DomainResult<Unit> =
        runCatchingDomainResultSuspend {
            if (dbFileAccessor.needsExternalDriverReset) {
                sharedDatabase.reset()
            }
            check(dbFileAccessor.importDatabaseClean(data)) {
                "Clean import failed: unable to write database file"
            }
            sharedDatabase.notifyAllQueries()
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun importDatabaseAppend(data: ByteArray): DomainResult<Unit> {
        val tempPath =
            dbFileAccessor.writeTempFile(data)
                ?: return DomainResult.Failure(
                    AppError.InternalError("Append import is not supported on this platform"),
                )

        return runCatchingDomainResultSuspend {
            try {
                sharedDatabase.executeRaw("ATTACH DATABASE '$tempPath' AS import_db")

                // Tables with is_default — skip default/system rows
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO bank_accounts " +
                        "SELECT * FROM import_db.bank_accounts WHERE is_default = 0",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO categories " +
                        "SELECT * FROM import_db.categories WHERE is_default = 0",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO currencies " +
                        "SELECT * FROM import_db.currencies WHERE is_default = 0",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO bank_names " +
                        "SELECT * FROM import_db.bank_names WHERE is_default = 0",
                )

                // Tables without is_default — copy all, dedup by primary key
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO transactions SELECT * FROM import_db.transactions",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO transaction_categories " +
                        "SELECT * FROM import_db.transaction_categories",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO exchange_rates SELECT * FROM import_db.exchange_rates",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO financial_sessions " +
                        "SELECT * FROM import_db.financial_sessions",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO session_account_snapshots " +
                        "SELECT * FROM import_db.session_account_snapshots",
                )
                sharedDatabase.executeRaw(
                    "INSERT OR IGNORE INTO attachments SELECT * FROM import_db.attachments",
                )

                sharedDatabase.executeRaw("DETACH DATABASE import_db")
                sharedDatabase.notifyAllQueries()
            } finally {
                dbFileAccessor.deleteTempFile(tempPath)
            }
        }
    }
}
