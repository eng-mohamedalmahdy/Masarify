package tech.lightfeather.masarify.framework

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write

internal actual suspend fun saveBackupFile(
    bytes: ByteArray,
    fileName: String,
): Boolean {
    val file =
        FileKit.openFileSaver(
            suggestedName = fileName.substringBeforeLast("."),
            extension = fileName.substringAfterLast("."),
        ) ?: return false
    file write bytes
    return true
}
