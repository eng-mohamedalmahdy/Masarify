package com.lightfeather.data.local.database

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class NativeDbFileAccessor : DbFileAccessor {
    private val documentsDir: String
        get() =
            NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true,
            ).first() as String

    private val dbPath: String get() = "$documentsDir/masarify.db"

    override val needsExternalDriverReset: Boolean = true

    override suspend fun exportDatabaseBytes(): ByteArray? {
        val data = NSData.dataWithContentsOfFile(dbPath) ?: return null
        return data.toByteArray()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun importDatabaseClean(bytes: ByteArray): Boolean =
        try {
            val data = bytes.toNSData()
            data.writeToFile(dbPath, atomically = true)
        } catch (e: Exception) {
            false
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun writeTempFile(bytes: ByteArray): String? =
        try {
            val path = "${NSTemporaryDirectory()}masarify_import.db"
            val data = bytes.toNSData()
            if (data.writeToFile(path, atomically = true)) path else null
        } catch (e: Exception) {
            null
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun deleteTempFile(path: String) {
        try {
            NSFileManager.defaultManager.removeItemAtPath(path, null)
        } catch (e: Exception) {
            // Best-effort cleanup; ignore failure
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val size = length.toInt()
        if (size == 0) return ByteArray(0)
        val result = ByteArray(size)
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
        return result
    }

    private fun ByteArray.toNSData(): NSData {
        if (isEmpty()) return NSData()
        return usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), size.convert())
        }
    }
}
