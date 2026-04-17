package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.BackupRepository

class ExportDataUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(): DomainResult<ByteArray> = backupRepository.exportDatabase()
}
