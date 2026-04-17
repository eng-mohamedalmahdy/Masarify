package com.lightfeather.data.local.database

interface DbFileAccessor {
    /**
     * Whether the platform requires the SQLDelight driver to be closed and reset
     * before writing new DB bytes during a clean import.
     * False for WASM where the worker manages the DB lifecycle internally.
     */
    val needsExternalDriverReset: Boolean

    /** Reads the current database file bytes. Returns null if unavailable. */
    suspend fun exportDatabaseBytes(): ByteArray?

    /**
     * Writes the given bytes as the database file.
     * On non-WASM platforms: overwrites the DB file (call [needsExternalDriverReset] first).
     * On WASM: sends import_clean to the worker, which handles close/write/reopen.
     */
    suspend fun importDatabaseClean(bytes: ByteArray): Boolean

    /**
     * Writes bytes to a temporary file and returns its absolute path.
     * Returns null on platforms where temp file access is unsupported (WASM).
     */
    suspend fun writeTempFile(bytes: ByteArray): String?

    /** Deletes a temp file previously created by [writeTempFile]. */
    suspend fun deleteTempFile(path: String)
}
