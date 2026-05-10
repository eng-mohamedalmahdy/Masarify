package tech.lightfeather.data.local.database.drivers

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    suspend fun createDriver(name: String): SqlDriver
}
