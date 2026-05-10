package tech.lightfeather.data.local.database

import android.content.Context
import java.io.File

class AndroidDbFileAccessor(
    private val context: Context,
) : DbFileAccessor {
    override val needsExternalDriverReset: Boolean = true

    override suspend fun exportDatabaseBytes(): ByteArray? {
        val dbFile = context.getDatabasePath("masarify.db")
        return if (dbFile.exists()) dbFile.readBytes() else null
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun importDatabaseClean(bytes: ByteArray): Boolean =
        try {
            val dbFile = context.getDatabasePath("masarify.db")
            dbFile.parentFile?.mkdirs()
            dbFile.writeBytes(bytes)
            true
        } catch (e: Exception) {
            false
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun writeTempFile(bytes: ByteArray): String? =
        try {
            val tempFile = File(context.cacheDir, "masarify_import_${System.currentTimeMillis()}.db")
            tempFile.writeBytes(bytes)
            tempFile.absolutePath
        } catch (e: Exception) {
            null
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
