package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.ReminderType

interface NotificationRepository {
    var lastAppOpenEpochDay: Long

    // App open tracking
    fun getAppOpenCount(): Int

    fun incrementAppOpenCount()

    // First-open timestamp (epoch ms, 0L = never set)
    fun getFirstOpenTimestamp(): Long

    fun setFirstOpenTimestamp(ts: Long)

    // In-app notification permission banner
    fun hasNotificationBannerBeenDismissed(): Boolean

    fun markNotificationBannerDismissed()

    // Dismissed tip IDs (0–6)
    fun getDismissedTipIds(): Set<Int>

    fun dismissTip(tipId: Int)

    // Master reminders toggle
    fun isRemindersEnabled(): Boolean

    fun setRemindersEnabled(enabled: Boolean)

    // Per-type reminder toggles
    fun isReminderEnabled(type: ReminderType): Boolean

    fun setReminderEnabled(
        type: ReminderType,
        enabled: Boolean,
    )

    // Daily reminder time (minutes from midnight, default = 21 * 60 = 9 PM)
    fun getDailyReminderMinutes(): Int

    fun setDailyReminderMinutes(minutes: Int)

    // Last-fired timestamps per reminder type (epoch ms)
    fun getLastReminderFiredAt(type: ReminderType): Long

    fun setLastReminderFiredAt(
        type: ReminderType,
        ts: Long,
    )

    // Last app-open date (epoch day number, for DAILY_EXPENSE_LOG skip logic)
    fun getLastAppOpenEpochDay(): Int

    fun setLastAppOpenEpochDay(day: Int)

    // Idle re-engagement: one nudge per streak
    fun getIdleStreakNudgeSent(): Boolean

    fun setIdleStreakNudgeSent(sent: Boolean)

    fun clearIdleStreakNudge()
}
