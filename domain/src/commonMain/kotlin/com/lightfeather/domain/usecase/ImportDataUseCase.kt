package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.ImportMode
import com.lightfeather.domain.repository.BackupRepository

class ImportDataUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(
        data: ByteArray,
        mode: ImportMode,
    ): DomainResult<Unit> =
        when (mode) {
            ImportMode.CLEAN -> backupRepository.importDatabaseClean(data)
            ImportMode.APPEND -> backupRepository.importDatabaseAppend(data)
        }
}
