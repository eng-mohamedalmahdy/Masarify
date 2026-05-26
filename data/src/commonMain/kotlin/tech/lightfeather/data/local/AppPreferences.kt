package tech.lightfeather.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.UserData

class AppPreferences(
    private val settings: Settings,
) {
    companion object {
        const val USER_DATA = "userData"
        const val DARK_MODE = "darkMode"
        const val DYNAMIC_COLORS = "dynamicColors"
        const val APP_LANGUAGE = "appLanguage"

        const val USER_SAVED_COLORS = "userSavedColors"
        const val DATA_SEEDED = "dataSeeded"
        const val BIOMETRIC_ENABLED = "biometricEnabled"
        const val BIOMETRIC_SUGGESTION_SHOWN = "biometricSuggestionShown"
        const val AUTO_SYNC_RATES = "autoSyncRates"
        const val AUTO_SYNC_DATA = "autoSyncData"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val REMOTE_USER_ID = "remoteUserId"
        const val LAST_SYNC_AT = "lastSyncAt"
        const val FCM_TOKEN = "fcmToken"
        const val FCM_PLATFORM = "fcmPlatform"
        const val IS_EMAIL_VERIFIED = "isEmailVerified"
        const val ONBOARDING_COMPLETE = "onboardingComplete"

        // Notifications
        const val APP_OPEN_COUNT = "appOpenCount"
        const val FIRST_OPEN_TS = "firstOpenTs"
        const val NOTIFICATION_BANNER_DISMISSED = "notifBannerDismissed"
        const val DISMISSED_TIP_IDS = "dismissedTipIds"
        const val REMINDERS_ENABLED = "remindersEnabled"
        const val REMINDER_DAILY_ENABLED = "reminderDaily"
        const val REMINDER_WEEKLY_ENABLED = "reminderWeekly"
        const val REMINDER_IDLE_ENABLED = "reminderIdle"
        const val REMINDER_MONTHLY_ENABLED = "reminderMonthly"
        const val DAILY_REMINDER_MINUTES = "dailyReminderMinutes"
        const val LAST_REMINDER_DAILY_TS = "lastReminderDailyTs"
        const val LAST_REMINDER_WEEKLY_TS = "lastReminderWeeklyTs"
        const val LAST_REMINDER_IDLE_TS = "lastReminderIdleTs"
        const val LAST_REMINDER_MONTHLY_TS = "lastReminderMonthlyTs"
        const val LAST_APP_OPEN_EPOCH_DAY = "lastAppOpenEpochDay"
        const val IDLE_STREAK_NUDGE_SENT = "idleStreakNudgeSent"
    }

    var userData: UserData?
        set(value) = settings.encodeValue(USER_DATA, value)
        get() = settings.decodeValueOrNull<UserData>(USER_DATA)

    var isDarkMode: Boolean
        set(value) = settings.set(DARK_MODE, value)
        get() = settings[DARK_MODE] ?: false

    var isDynamicColors: Boolean
        set(value) = settings.set(DYNAMIC_COLORS, value)
        get() = settings[DYNAMIC_COLORS] ?: false

    var appLanguage: AppLanguage?
        set(value) = settings.encodeValue(APP_LANGUAGE, value)
        get() = settings.decodeValueOrNull<AppLanguage>(APP_LANGUAGE)

    var userSavedColors: List<String>
        set(value) = settings.encodeValue(USER_SAVED_COLORS, value)
        get() = settings.decodeValueOrNull<List<String>>(USER_SAVED_COLORS) ?: emptyList()

    var isDataSeeded: Boolean
        set(value) = settings.set(DATA_SEEDED, value)
        get() = settings[DATA_SEEDED] ?: false

    var isBiometricEnabled: Boolean
        set(value) = settings.set(BIOMETRIC_ENABLED, value)
        get() = settings[BIOMETRIC_ENABLED] ?: false

    var hasShownBiometricSuggestion: Boolean
        set(value) = settings.set(BIOMETRIC_SUGGESTION_SHOWN, value)
        get() = settings[BIOMETRIC_SUGGESTION_SHOWN] ?: false

    var isAutoSyncRatesEnabled: Boolean
        set(value) = settings.set(AUTO_SYNC_RATES, value)
        get() = settings[AUTO_SYNC_RATES] ?: true

    var isAutoSyncDataEnabled: Boolean
        set(value) = settings.set(AUTO_SYNC_DATA, value)
        get() = settings[AUTO_SYNC_DATA] ?: true

    var accessToken: String?
        set(value) = if (value != null) settings.set(ACCESS_TOKEN, value) else settings.remove(ACCESS_TOKEN)
        get() = settings[ACCESS_TOKEN]

    var refreshToken: String?
        set(value) = if (value != null) settings.set(REFRESH_TOKEN, value) else settings.remove(REFRESH_TOKEN)
        get() = settings[REFRESH_TOKEN]

    var remoteUserId: Long?
        set(value) = if (value != null) settings.set(REMOTE_USER_ID, value) else settings.remove(REMOTE_USER_ID)
        get() = settings[REMOTE_USER_ID]

    var lastSyncAt: Long
        set(value) = settings.set(LAST_SYNC_AT, value)
        get() = settings[LAST_SYNC_AT] ?: 0L

    var fcmToken: String?
        set(value) = if (value != null) settings.set(FCM_TOKEN, value) else settings.remove(FCM_TOKEN)
        get() = settings[FCM_TOKEN]

    var fcmPlatform: String?
        set(value) = if (value != null) settings.set(FCM_PLATFORM, value) else settings.remove(FCM_PLATFORM)
        get() = settings[FCM_PLATFORM]

    var isEmailVerified: Boolean
        set(value) = settings.set(IS_EMAIL_VERIFIED, value)
        get() = settings[IS_EMAIL_VERIFIED] ?: false

    var isOnboardingComplete: Boolean
        set(value) = settings.set(ONBOARDING_COMPLETE, value)
        get() = settings[ONBOARDING_COMPLETE] ?: false

    var appOpenCount: Int
        set(value) = settings.set(APP_OPEN_COUNT, value)
        get() = settings[APP_OPEN_COUNT] ?: 0

    var firstOpenTimestamp: Long
        set(value) = settings.set(FIRST_OPEN_TS, value)
        get() = settings[FIRST_OPEN_TS] ?: 0L

    var isNotificationBannerDismissed: Boolean
        set(value) = settings.set(NOTIFICATION_BANNER_DISMISSED, value)
        get() = settings[NOTIFICATION_BANNER_DISMISSED] ?: false

    var dismissedTipIds: Set<Int>
        set(value) = settings.set(DISMISSED_TIP_IDS, value.joinToString(","))
        get() {
            val raw: String = settings[DISMISSED_TIP_IDS] ?: return emptySet()
            return raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }

    var isRemindersEnabled: Boolean
        set(value) = settings.set(REMINDERS_ENABLED, value)
        get() = settings[REMINDERS_ENABLED] ?: true

    var isDailyExpenseLogReminderEnabled: Boolean
        set(value) = settings.set(REMINDER_DAILY_ENABLED, value)
        get() = settings[REMINDER_DAILY_ENABLED] ?: true

    var isWeeklySummaryReminderEnabled: Boolean
        set(value) = settings.set(REMINDER_WEEKLY_ENABLED, value)
        get() = settings[REMINDER_WEEKLY_ENABLED] ?: true

    var isIdleReEngagementReminderEnabled: Boolean
        set(value) = settings.set(REMINDER_IDLE_ENABLED, value)
        get() = settings[REMINDER_IDLE_ENABLED] ?: true

    var isMonthlyRecapReminderEnabled: Boolean
        set(value) = settings.set(REMINDER_MONTHLY_ENABLED, value)
        get() = settings[REMINDER_MONTHLY_ENABLED] ?: true

    var dailyReminderMinutes: Int
        set(value) = settings.set(DAILY_REMINDER_MINUTES, value)
        get() = settings[DAILY_REMINDER_MINUTES] ?: (21 * 60)

    var lastReminderDailyTs: Long
        set(value) = settings.set(LAST_REMINDER_DAILY_TS, value)
        get() = settings[LAST_REMINDER_DAILY_TS] ?: 0L

    var lastReminderWeeklyTs: Long
        set(value) = settings.set(LAST_REMINDER_WEEKLY_TS, value)
        get() = settings[LAST_REMINDER_WEEKLY_TS] ?: 0L

    var lastReminderIdleTs: Long
        set(value) = settings.set(LAST_REMINDER_IDLE_TS, value)
        get() = settings[LAST_REMINDER_IDLE_TS] ?: 0L

    var lastReminderMonthlyTs: Long
        set(value) = settings.set(LAST_REMINDER_MONTHLY_TS, value)
        get() = settings[LAST_REMINDER_MONTHLY_TS] ?: 0L

    var lastAppOpenEpochDay: Int
        set(value) = settings.set(LAST_APP_OPEN_EPOCH_DAY, value)
        get() = settings[LAST_APP_OPEN_EPOCH_DAY] ?: 0

    var isIdleStreakNudgeSent: Boolean
        set(value) = settings.set(IDLE_STREAK_NUDGE_SENT, value)
        get() = settings[IDLE_STREAK_NUDGE_SENT] ?: false

    fun clearAuthTokens() {
        settings.remove(ACCESS_TOKEN)
        settings.remove(REFRESH_TOKEN)
        settings.remove(REMOTE_USER_ID)
        settings.remove(IS_EMAIL_VERIFIED)
    }
}
