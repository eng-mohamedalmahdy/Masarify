package com.lightfeather.domain.repository

import com.lightfeather.domain.model.DomainResult

interface BackupRepository {
    suspend fun exportDatabase(): DomainResult<ByteArray>

    suspend fun importDatabaseClean(data: ByteArray): DomainResult<Unit>

    suspend fun importDatabaseAppend(data: ByteArray): DomainResult<Unit>
}
