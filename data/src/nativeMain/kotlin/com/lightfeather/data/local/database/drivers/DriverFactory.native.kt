package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.lightfeather.masarify.database.Database

actual class DriverFactory {
    actual suspend fun createDriver(name: String): SqlDriver {
        return NativeSqliteDriver(Database.Schema.synchronous(),name)
    }
}