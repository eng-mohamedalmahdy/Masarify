package com.lightfeather.data.local.database

import java.io.File
import java.nio.file.FileSystems

class JvmDbFileAccessor(
    private val appPath: String,
) : DbFileAccessor {
    private val separator: String = FileSystems.getDefault().separator
    private val dbFile: File get() = File("$appPath${separator}masarify.db")

    override val needsExternalDriverReset: Boolean = true

    override suspend fun exportDatabaseBytes(): ByteArray? {
        val f = dbFile
        return if (f.exists()) f.readBytes() else null
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun importDatabaseClean(bytes: ByteArray): Boolean =
        try {
            dbFile.writeBytes(bytes)
            true
        } catch (e: Exception) {
            false
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun writeTempFile(bytes: ByteArray): String? {
        return try {
            val tempDir = System.getProperty("java.io.tmpdir") ?: return null
            val tempFile = File(tempDir, "masarify_import_${System.currentTimeMillis()}.db")
            tempFile.writeBytes(bytes)
            tempFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun deleteTempFile(path: String) {
        try {
            File(path).delete()
        } catch (e: Exception) {
            // Best-effort cleanup; ignore failure
        }
    }
}
