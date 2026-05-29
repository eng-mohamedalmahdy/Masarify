package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionStatus

/**
 * Platform abstraction for RevenueCat SDK operations.
 * Implementations live in data module per-platform source sets.
 * Android/iOS: wraps the native RC SDK.
 * WASM: stub that always returns FREE (no billing on web).
 */
interface RevenueCatSdk {
    /**
     * Initiates the Pro purchase flow.
     * Returns the RC customerId on success so the backend can be linked.
     */
    suspend fun purchasePro(): DomainResult<String>

    /**
     * Restores previous purchases from the app store.
     * Returns the RC customerId on success.
     */
    suspend fun restorePurchases(): DomainResult<String>

    /**
     * Fetches the current subscription status directly from RC.
     */
    suspend fun getCustomerInfo(): DomainResult<SubscriptionStatus>

    /**
     * Initializes the RC SDK. Call after user ID is known.
     * @param apiKey Platform-specific RC API key.
     * @param userId Masarify userId as string, or null for anonymous.
     */
    fun initialize(
        apiKey: String,
        userId: String?,
    )
}
