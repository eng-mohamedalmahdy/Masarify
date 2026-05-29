package tech.lightfeather.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.lightfeather.data.local.database.drivers.SharedDatabase
import tech.lightfeather.data.remote.SubscriptionApi
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.SubscriptionStatusType
import tech.lightfeather.domain.repository.SubscriptionRepository
import kotlin.time.Clock

class SubscriptionRepositoryImpl(
    private val subscriptionApi: SubscriptionApi,
    private val database: SharedDatabase,
) : SubscriptionRepository {
    override fun getStatus(): Flow<DomainResult<SubscriptionStatus>> = flow {
        val cached = database { it.subscriptionCacheQueries.get().executeAsOneOrNull() }
        if (cached != null) {
            emit(DomainResult.Success(cached.toDomain()))
        }

        val now = Clock.System.now().toEpochMilliseconds()
        val networkResult = subscriptionApi.getStatus()
            .mapSuspend { status ->
                database { db ->
                    db.subscriptionCacheQueries.upsert(
                        plan = status.plan.name,
                        status = status.status.name,
                        expires_at = status.expiresAt,
                        cached_at = now,
                    )
                }
                status
            }

        when {
            networkResult.isSuccess -> emit(networkResult)
            cached == null -> emit(networkResult)
            // else: network failed but cache already emitted — silent refresh failure
        }
    }

    override suspend fun linkRevenueCatCustomer(rcCustomerId: String): DomainResult<Unit> =
        subscriptionApi.linkCustomer(rcCustomerId)

    override suspend fun getCachedStatusAge(): Long {
        val cached = database { it.subscriptionCacheQueries.get().executeAsOneOrNull() }
        return cached?.cached_at ?: 0L
    }
}

private fun lightfeather.masarify.database.Subscription_cache.toDomain() =
    SubscriptionStatus(
        plan = runCatching { SubscriptionPlan.valueOf(plan) }.getOrElse { SubscriptionPlan.FREE },
        status = runCatching { SubscriptionStatusType.valueOf(status) }.getOrElse { SubscriptionStatusType.NONE },
        expiresAt = expires_at,
    )
