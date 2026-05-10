package tech.lightfeather.masarify.fcm

import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.domain.repository.UserRepository

class WasmFcmHelper(private val userRepository: UserRepository) : FCMHelper {
    override fun getFirebaseToken(): String? = userRepository.getFcmToken()
}
