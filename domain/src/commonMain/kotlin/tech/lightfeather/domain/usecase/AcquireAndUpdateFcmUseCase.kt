package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.domain.repository.Platform
import tech.lightfeather.domain.repository.UserRepository

data class AcquireAndUpdateFcmUseCase(
    private val platform: Platform,
    private val fcmHelper: FCMHelper,
    private val userRepository: UserRepository,
    private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        val platformName = platform.name
        val token =
            fcmHelper.getFirebaseToken() ?: return DomainResult.Failure(AppError.InternalError("FCM token is null"))
        userRepository.syncFcmToken(token, platformName)
        registerDeviceTokenUseCase(token, platformName)
        return DomainResult.Success(Unit)
    }
}
