package com.lightfeather.data.database.drivers

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lightfeather.masarify.database.Database

actual class DriverFactory {
    actual suspend fun createDriver(name: String): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { Database.Schema.create(it).await() }
    }
}