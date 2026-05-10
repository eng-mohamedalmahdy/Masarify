package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.BackupRepository
import tech.lightfeather.domain.repository.UserRepository

class ClearLocalDataUseCase(
    private val backupRepository: BackupRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): DomainResult<Unit> =
        backupRepository.clearUserData().also {
            if (it is DomainResult.Success) {
                userRepository.setLastSyncAt(0L)
                userRepository.clearFcmToken()
            }
        }
}
