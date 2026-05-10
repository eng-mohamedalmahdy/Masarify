package tech.lightfeather.masarify.framework

/**
 * Opens a system save dialog and writes [bytes] to the chosen location.
 * On WASM/web, triggers a browser download instead.
 * Returns true if the file was saved successfully, false otherwise.
 */
internal expect suspend fun saveBackupFile(
    bytes: ByteArray,
    fileName: String,
): Boolean
