package com.lightfeather.data.local.database.drivers

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.lightfeather.masarify.database.Database
import org.w3c.dom.Worker

actual class DriverFactory {
    actual suspend fun createDriver(name: String): SqlDriver {
        val driver: SqlDriver = WebWorkerDriver(jsWorker())
        Database.Schema.awaitCreate(driver)
        driver.execute(null, "PRAGMA journal_mode=WAL", 0, null)
        return driver
//        return createDefaultWebWorkerDriver()
//            .also { Database.Schema.create(it).await() }
//            .also  {
//                // Important: Give your database a name for persistence
//                it.execute(null, "PRAGMA journal_mode=WAL", 0, null)
//            }
    }
}

fun jsWorker(): Worker =
    js(
        """
    (function() {
        var w = new Worker(new URL("masarifyworker.worker.js", import.meta.url));
        globalThis.__masarifyDbWorker = w;
        return w;
    })()
    """,
    )
