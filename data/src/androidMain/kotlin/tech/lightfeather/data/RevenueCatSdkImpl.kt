package tech.lightfeather.data

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import io.github.aakira.napier.Napier
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.domain.model.SubscriptionStatus
import tech.lightfeather.domain.model.SubscriptionStatusType
import tech.lightfeather.domain.model.error.AppError
import tech.lightfeather.domain.repository.RevenueCatSdk
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val PRO_ENTITLEMENT_KEY = "Masarify Pro"

class RevenueCatSdkImpl : RevenueCatSdk {
    override fun initialize(
        apiKey: String,
        userId: String?,
    ) {
        Purchases.configure(
            PurchasesConfiguration(apiKey = apiKey) {
                if (userId != null) appUserId = userId
            },
        )
        Napier.i("RevenueCat configured userId=$userId", tag = "RevenueCatSdk")
    }

    @Suppress("ReturnCount")
    override suspend fun purchasePro(): DomainResult<String> {
        val offerings =
            getOfferings()
                ?: return DomainResult.Failure(AppError.InternalError("Offerings unavailable"))

        val pkg =
            offerings.current?.monthly
                ?: return DomainResult.Failure(AppError.InternalError("No monthly offering available"))

        return purchasePackage(pkg)
    }

    override suspend fun restorePurchases(): DomainResult<String> =
        suspendCoroutine { cont ->
            Purchases.sharedInstance.restorePurchases(
                onError = { error ->
                    Napier.e("RC restore error: $error", tag = "RevenueCatSdk")
                    cont.resume(DomainResult.Failure(AppError.InternalError(error.message)))
                },
                onSuccess = { customerInfo ->
                    cont.resume(DomainResult.Success(customerInfo.originalAppUserId))
                },
            )
        }

    override suspend fun getCustomerInfo(): DomainResult<SubscriptionStatus> =
        suspendCoroutine { cont ->
            Purchases.sharedInstance.getCustomerInfo(
                onError = { error ->
                    Napier.e("RC getCustomerInfo error: $error", tag = "RevenueCatSdk")
                    cont.resume(DomainResult.Failure(AppError.InternalError(error.message)))
                },
                onSuccess = { customerInfo ->
                    cont.resume(DomainResult.Success(customerInfo.toSubscriptionStatus()))
                },
            )
        }

    private suspend fun getOfferings(): Offerings? =
        suspendCoroutine { cont ->
            Purchases.sharedInstance.getOfferings(
                onError = { error ->
                    Napier.e("RC getOfferings error: $error", tag = "RevenueCatSdk")
                    cont.resume(null)
                },
                onSuccess = { cont.resume(it) },
            )
        }

    private suspend fun purchasePackage(pkg: Package): DomainResult<String> =
        suspendCoroutine { cont ->
            Purchases.sharedInstance.purchase(
                packageToPurchase = pkg,
                onError = { error, userCancelled ->
                    Napier.e("RC purchase error: $error cancelled=$userCancelled", tag = "RevenueCatSdk")
                    val msg = if (userCancelled) "cancelled" else error.message
                    cont.resume(DomainResult.Failure(AppError.InternalError(msg)))
                },
                onSuccess = { _, customerInfo ->
                    cont.resume(DomainResult.Success(customerInfo.originalAppUserId))
                },
            )
        }

    private fun CustomerInfo.toSubscriptionStatus(): SubscriptionStatus {
        val entitlement = entitlements[PRO_ENTITLEMENT_KEY]
        return if (entitlement?.isActive == true) {
            SubscriptionStatus(
                plan = SubscriptionPlan.PRO,
                status = SubscriptionStatusType.ACTIVE,
                expiresAt = null, // backend tracks accurate expiry via webhook
            )
        } else {
            SubscriptionStatus.FREE_DEFAULT
        }
    }
}
