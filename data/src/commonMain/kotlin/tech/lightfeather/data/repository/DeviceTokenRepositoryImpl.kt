package tech.lightfeather.data.repository

import tech.lightfeather.data.remote.DeviceApi
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.DeviceTokenRepository

class DeviceTokenRepositoryImpl(
    private val deviceApi: DeviceApi,
) : DeviceTokenRepository {
    override suspend fun register(
        token: String,
        platform: String,
    ): DomainResult<Unit> = deviceApi.registerToken(token, platform)
}
