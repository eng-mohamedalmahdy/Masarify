package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult

interface DeviceTokenRepository {
    suspend fun register(
        token: String,
        platform: String,
    ): DomainResult<Unit>
}
