package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.repository.RevenueCatSdk
import tech.lightfeather.domain.repository.SubscriptionRepository

class GetSubscriptionStatusUseCase(
    private val subscriptionRepository: SubscriptionRepository,
) {
    operator fun invoke(): Flow<DomainResult<SubscriptionStatus>> = subscriptionRepository.getStatus()
}

class IsProActiveUseCase(
    private val subscriptionRepository: SubscriptionRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        subscriptionRepository.getStatus()
            .map { result -> result.getOrElse(SubscriptionStatus.FREE_DEFAULT).isProActive }
}

class PurchaseProUseCase(
    private val revenueCatSdk: RevenueCatSdk,
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend operator fun invoke(): DomainResult<Unit> =
        revenueCatSdk.purchasePro().flatMapSuspend { rcCustomerId ->
            subscriptionRepository.linkRevenueCatCustomer(rcCustomerId)
        }
}

class RestorePurchasesUseCase(
    private val revenueCatSdk: RevenueCatSdk,
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend operator fun invoke(): DomainResult<Unit> =
        revenueCatSdk.restorePurchases().flatMapSuspend { rcCustomerId ->
            subscriptionRepository.linkRevenueCatCustomer(rcCustomerId)
        }
}

class LinkRevenueCatCustomerUseCase(
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend operator fun invoke(rcCustomerId: String): DomainResult<Unit> =
        subscriptionRepository.linkRevenueCatCustomer(rcCustomerId)
}

class InitializeRevenueCatUseCase(
    private val revenueCatSdk: RevenueCatSdk,
) {
    operator fun invoke(
        apiKey: String,
        userId: String?,
    ) = revenueCatSdk.initialize(apiKey, userId)
}

class UpgradeRequiredException : Exception("Pro subscription required")

@Suppress("TooGenericExceptionCaught")
suspend fun <T> DomainResult<T>.throwIfUpgradeRequired(): DomainResult<T> {
    if (this is DomainResult.Failure && error is AppError.UpgradeRequired) {
        throw UpgradeRequiredException()
    }
    return this
}
