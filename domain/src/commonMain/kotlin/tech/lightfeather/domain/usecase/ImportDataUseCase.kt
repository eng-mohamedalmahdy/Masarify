package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.ImportMode
import tech.lightfeather.domain.repository.BackupRepository

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
