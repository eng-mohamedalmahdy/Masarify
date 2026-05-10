package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.BackupRepository

class ExportDataUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(): DomainResult<ByteArray> = backupRepository.exportDatabase()
}
