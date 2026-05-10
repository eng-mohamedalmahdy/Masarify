package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult

interface BackupRepository {
    suspend fun exportDatabase(): DomainResult<ByteArray>

    suspend fun importDatabaseClean(data: ByteArray): DomainResult<Unit>

    suspend fun importDatabaseAppend(data: ByteArray): DomainResult<Unit>

    suspend fun clearUserData(): DomainResult<Unit>
}
