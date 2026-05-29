package tech.lightfeather.masarify.page.paywall

import tech.lightfeather.domain.model.SubscriptionPlan

internal data class PaywallPageState(
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val currentPlan: SubscriptionPlan = SubscriptionPlan.FREE,
    val expiresAt: Long? = null,
)
