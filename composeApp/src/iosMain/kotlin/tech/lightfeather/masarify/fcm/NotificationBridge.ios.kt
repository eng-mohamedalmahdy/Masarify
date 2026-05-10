package tech.lightfeather.masarify.fcm

import tech.lightfeather.domain.repository.UserRepository
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.RegisterDeviceTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual object NotificationBridge : KoinComponent {

    actual fun onNewFcmToken(token: String, platform: String) {
        val useCase: RegisterDeviceTokenUseCase by inject()
        val userRepository: UserRepository by inject()
        CoroutineScope(Dispatchers.Default).launch {
            userRepository.syncFcmToken(token, platform)
            useCase(token, platform)
        }
    }

    actual fun onSyncRequested() {
        val drainUseCase: DrainOutboxQueueUseCase by inject()
        val pullUseCase: PullRemoteDeltaUseCase by inject()
        CoroutineScope(Dispatchers.Default).launch {
            drainUseCase()
            pullUseCase()
        }
    }
}
