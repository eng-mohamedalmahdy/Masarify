package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.DeviceTokenRepository

class RegisterDeviceTokenUseCase(
    private val repository: DeviceTokenRepository,
) {
    suspend operator fun invoke(
        token: String,
        platform: String,
    ): DomainResult<Unit> = repository.register(token, platform)
}
