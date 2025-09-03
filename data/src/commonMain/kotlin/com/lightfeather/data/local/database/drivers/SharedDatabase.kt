package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.async.coroutines.awaitCreate
import com.lightfeather.masarify.database.Database

class SharedDatabase(
    private val driverFactory: DriverFactory,
) {
    lateinit var database: Database

    private suspend fun initDatabase() {
        if (!::database.isInitialized) {
            val driver = driverFactory.createDriver("masarify.db")
            database = Database(driver).also {
                Database.Schema.awaitCreate(driver)
            }
        }
    }

    suspend operator fun <R> invoke(block: suspend (Database) -> R): R {
        initDatabase()
        return block(database)
    }
}