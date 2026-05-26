package tech.lightfeather.domain.model

data class NotificationSettings(
    val isRemindersEnabled: Boolean = true,
    val isDailyExpenseLogEnabled: Boolean = true,
    val isWeeklySummaryEnabled: Boolean = true,
    val isIdleReEngagementEnabled: Boolean = true,
    val isMonthlyRecapEnabled: Boolean = true,
    val dailyReminderMinutes: Int = 21 * 60,
)
