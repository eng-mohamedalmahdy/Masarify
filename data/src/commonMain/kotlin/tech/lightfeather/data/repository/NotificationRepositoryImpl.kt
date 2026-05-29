package tech.lightfeather.data.repository

import tech.lightfeather.data.local.AppPreferences
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val prefs: AppPreferences,
) : NotificationRepository {
    override var lastAppOpenEpochDay: Long = prefs.lastAppOpenEpochDay.toLong()

    override fun getAppOpenCount(): Int = prefs.appOpenCount

    override fun incrementAppOpenCount() {
        prefs.appOpenCount += 1
    }

    override fun getFirstOpenTimestamp(): Long = prefs.firstOpenTimestamp

    override fun setFirstOpenTimestamp(ts: Long) {
        prefs.firstOpenTimestamp = ts
    }

    override fun hasNotificationBannerBeenDismissed(): Boolean = prefs.isNotificationBannerDismissed

    override fun markNotificationBannerDismissed() {
        prefs.isNotificationBannerDismissed = true
    }

    override fun getDismissedTipIds(): Set<Int> = prefs.dismissedTipIds

    override fun dismissTip(tipId: Int) {
        prefs.dismissedTipIds = prefs.dismissedTipIds + tipId
    }

    override fun isRemindersEnabled(): Boolean = prefs.isRemindersEnabled

    override fun setRemindersEnabled(enabled: Boolean) {
        prefs.isRemindersEnabled = enabled
    }

    override fun isReminderEnabled(type: ReminderType): Boolean =
        when (type) {
            ReminderType.DAILY_EXPENSE_LOG -> prefs.isDailyExpenseLogReminderEnabled
            ReminderType.WEEKLY_SUMMARY -> prefs.isWeeklySummaryReminderEnabled
            ReminderType.IDLE_RE_ENGAGEMENT -> prefs.isIdleReEngagementReminderEnabled
            ReminderType.MONTHLY_RECAP -> prefs.isMonthlyRecapReminderEnabled
        }

    override fun setReminderEnabled(
        type: ReminderType,
        enabled: Boolean,
    ) {
        when (type) {
            ReminderType.DAILY_EXPENSE_LOG -> prefs.isDailyExpenseLogReminderEnabled = enabled
            ReminderType.WEEKLY_SUMMARY -> prefs.isWeeklySummaryReminderEnabled = enabled
            ReminderType.IDLE_RE_ENGAGEMENT -> prefs.isIdleReEngagementReminderEnabled = enabled
            ReminderType.MONTHLY_RECAP -> prefs.isMonthlyRecapReminderEnabled = enabled
        }
    }

    override fun getDailyReminderMinutes(): Int = prefs.dailyReminderMinutes

    override fun setDailyReminderMinutes(minutes: Int) {
        prefs.dailyReminderMinutes = minutes
    }

    override fun getLastReminderFiredAt(type: ReminderType): Long =
        when (type) {
            ReminderType.DAILY_EXPENSE_LOG -> prefs.lastReminderDailyTs
            ReminderType.WEEKLY_SUMMARY -> prefs.lastReminderWeeklyTs
            ReminderType.IDLE_RE_ENGAGEMENT -> prefs.lastReminderIdleTs
            ReminderType.MONTHLY_RECAP -> prefs.lastReminderMonthlyTs
        }

    override fun setLastReminderFiredAt(
        type: ReminderType,
        ts: Long,
    ) {
        when (type) {
            ReminderType.DAILY_EXPENSE_LOG -> prefs.lastReminderDailyTs = ts
            ReminderType.WEEKLY_SUMMARY -> prefs.lastReminderWeeklyTs = ts
            ReminderType.IDLE_RE_ENGAGEMENT -> prefs.lastReminderIdleTs = ts
            ReminderType.MONTHLY_RECAP -> prefs.lastReminderMonthlyTs = ts
        }
    }

    override fun getLastAppOpenEpochDay(): Int = prefs.lastAppOpenEpochDay

    override fun setLastAppOpenEpochDay(day: Int) {
        prefs.lastAppOpenEpochDay = day
    }

    override fun getIdleStreakNudgeSent(): Boolean = prefs.isIdleStreakNudgeSent

    override fun setIdleStreakNudgeSent(sent: Boolean) {
        prefs.isIdleStreakNudgeSent = sent
    }

    override fun clearIdleStreakNudge() {
        prefs.isIdleStreakNudgeSent = false
    }
}
