package tech.lightfeather.data

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.repository.RevenueCatSdk

// WASM/Web stub — no billing on web. Always returns FREE.
// Users are directed to the mobile app to subscribe.
class RevenueCatSdkImpl : RevenueCatSdk {
    override suspend fun purchasePro(): DomainResult<String> =
        DomainResult.Failure(AppError.InternalError("billing_not_available_on_web"))

    override suspend fun restorePurchases(): DomainResult<String> =
        DomainResult.Failure(AppError.InternalError("billing_not_available_on_web"))

    override suspend fun getCustomerInfo(): DomainResult<SubscriptionStatus> =
        DomainResult.Success(SubscriptionStatus.FREE_DEFAULT)

    override fun initialize(
        apiKey: String,
        userId: String?,
    ) {
        // no-op on web
    }
}
