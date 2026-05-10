package tech.lightfeather.data.local.database.drivers

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import tech.lightfeather.masarify.database.Database

actual class DriverFactory(
    private val context: Context,
) {
    actual suspend fun createDriver(name: String): SqlDriver =
        AndroidSqliteDriver(Database.Schema.synchronous(), context, name)
}
