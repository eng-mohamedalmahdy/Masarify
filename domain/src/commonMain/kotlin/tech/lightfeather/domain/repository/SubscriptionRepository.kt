package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionStatus

interface SubscriptionRepository {
    fun getStatus(): Flow<DomainResult<SubscriptionStatus>>

    suspend fun linkRevenueCatCustomer(rcCustomerId: String): DomainResult<Unit>

    suspend fun getCachedStatusAge(): Long
}
