package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import com.lightfeather.masarify.database.Database

actual class DriverFactory {
    actual suspend fun createDriver(name: String): SqlDriver {
        return createDefaultWebWorkerDriver().also { Database.Schema.create(it).await() }
    }
}