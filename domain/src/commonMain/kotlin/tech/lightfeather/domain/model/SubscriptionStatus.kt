package tech.lightfeather.domain.model

import kotlin.time.Clock

enum class SubscriptionPlan { FREE, PRO }

enum class SubscriptionStatusType { NONE, ACTIVE, CANCELLED, EXPIRED, GRACE_PERIOD }

data class SubscriptionStatus(
    val plan: SubscriptionPlan,
    val status: SubscriptionStatusType,
    val expiresAt: Long?,
) {
    val isProActive: Boolean
        get() {
            if (plan != SubscriptionPlan.PRO) return false
            if (status !in
                setOf(
                    SubscriptionStatusType.ACTIVE,
                    SubscriptionStatusType.CANCELLED,
                    SubscriptionStatusType.GRACE_PERIOD,
                )
            ) {
                return false
            }
            val now = Clock.System.now().toEpochMilliseconds()
            return expiresAt == null || expiresAt > now
        }

    companion object {
        val FREE_DEFAULT =
            SubscriptionStatus(
                plan = SubscriptionPlan.FREE,
                status = SubscriptionStatusType.NONE,
                expiresAt = null,
            )
    }
}
