package tech.lightfeather.data.local.database.drivers

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import tech.lightfeather.masarify.database.Database

class SharedDatabase(
    private val driverFactory: DriverFactory,
) {
    private var database: Database? = null

    // Exposed internally for reset/executeRaw/notifyAllQueries in BackupRepositoryImpl
    internal var activeDriver: SqlDriver? = null

    private suspend fun initDatabase() {
        if (database == null) {
            val driver = driverFactory.createDriver("masarify.db")
            activeDriver = driver
            val oldVersion = getVersion(driver)
            val newVersion = Database.Schema.version
            when {
                oldVersion == 0L -> {
                    // New database: create full schema at current version.
                    // Note: legacy v0 databases (pre-migration-tracking) need app data cleared.
                    Database.Schema.awaitCreate(driver)
                    setVersion(driver, newVersion)
                }
                oldVersion < newVersion -> {
                    Database.Schema.awaitMigrate(driver, oldVersion, newVersion)
                    setVersion(driver, newVersion)
                }
            }
            database = Database(driver)
        }
    }

    private suspend fun getVersion(driver: SqlDriver): Long =
        driver
            .executeQuery(
                identifier = null,
                sql = "PRAGMA user_version",
                mapper = { cursor ->
                    val next = cursor.next()
                    if (next is QueryResult.Value) {
                        // Synchronous driver (Android, JVM, iOS)
                        QueryResult.Value(cursor.getLong(0) ?: 0L)
                    } else {
                        // Asynchronous driver (WASM)
                        QueryResult.AsyncValue {
                            next.await()
                            cursor.getLong(0) ?: 0L
                        }
                    }
                },
                parameters = 0,
                binders = null,
            ).await()

    private suspend fun setVersion(
        driver: SqlDriver,
        version: Long,
    ) {
        driver.execute(null, "PRAGMA user_version = $version", 0, null).await()
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

    @Suppress("SpreadOperator")
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
                "sync_queue",
            ),
        )
    }

    suspend operator fun <R> invoke(block: suspend (Database) -> R): R {
        initDatabase()
        return block(database!!)
    }
}
