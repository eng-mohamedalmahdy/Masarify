package tech.lightfeather.masarify.framework

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download

internal actual suspend fun saveBackupFile(
    bytes: ByteArray,
    fileName: String,
): Boolean {
    FileKit.download(bytes = bytes, fileName = fileName)
    return true
}
