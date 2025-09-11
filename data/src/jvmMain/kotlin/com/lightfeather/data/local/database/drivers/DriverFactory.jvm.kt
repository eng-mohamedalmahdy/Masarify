package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lightfeather.masarify.database.Database
import java.nio.file.FileSystems

actual class DriverFactory(
    private val appPath: String,
) {
    actual suspend fun createDriver(name: String): SqlDriver {
        val dbPath = appPath + FileSystems.getDefault().separator + name
        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:$dbPath")
        Database.Schema.create(driver)
        return driver
    }
}
