package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import com.lightfeather.masarify.database.Database

class SharedDatabase(
    private val driverFactory: DriverFactory,
) {
    private var database: Database? = null

    // Exposed internally for reset/executeRaw/notifyAllQueries in BackupRepositoryImpl
    internal var activeDriver: SqlDriver? = null

    private suspend fun initDatabase() {
        if (database == null) {
            activeDriver = driverFactory.createDriver("masarify.db")
            database =
                Database(activeDriver!!).also {
                    Database.Schema.awaitCreate(activeDriver!!)
                }
        }
    }

    suspend fun reset() {
        activeDriver?.close()
        activeDriver = null
        database = null
    }

    suspend fun executeRaw(sql: String) {
        initDatabase()
        activeDriver!!.execute(null, sql, 0, null).await()
    }

    fun notifyAllQueries() {
        activeDriver?.notifyListeners(
            *arrayOf(
                "transactions",
                "transaction_categories",
                "bank_accounts",
                "categories",
                "currencies",
                "exchange_rates",
                "financial_sessions",
                "session_account_snapshots",
                "bank_names",
                "attachments",
            ),
        )
    }

    suspend operator fun <R> invoke(block: suspend (Database) -> R): R {
        initDatabase()
        return block(database!!)
    }
}
