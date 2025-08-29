package com.lightfeather.data.database.drivers

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lightfeather.masarify.database.Database

actual class DriverFactory(private val context: Context) {
    actual suspend fun createDriver(name: String): SqlDriver {
        return AndroidSqliteDriver(Database.Schema.synchronous(), context, name)
    }
}