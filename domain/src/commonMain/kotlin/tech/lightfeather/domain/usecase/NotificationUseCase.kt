package tech.lightfeather.domain.usecase

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import tech.lightfeather.domain.model.NotificationSettings
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.repository.NotificationRepository
import kotlin.time.Clock

class GetActiveTip(
    private val repository: NotificationRepository,
) {
    operator fun invoke(): Int? {
        val firstOpenTs = repository.getFirstOpenTimestamp()
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        val isInWindow = firstOpenTs > 0L &&
            Clock.System.now().toEpochMilliseconds() - firstOpenTs <= thirtyDaysMs
        if (!isInWindow) return null
        val dismissed = repository.getDismissedTipIds()
        return (0..6).firstOrNull { it !in dismissed }
    }
}

class DismissNotificationTip(
    private val repository: NotificationRepository,
) {
    operator fun invoke(tipId: Int) = repository.dismissTip(tipId)
}

class ShouldShowNotificationBanner(
    private val repository: NotificationRepository,
) {
    operator fun invoke(): Boolean =
        repository.getAppOpenCount() >= 3 && !repository.hasNotificationBannerBeenDismissed()
}

class DismissNotificationBanner(
    private val repository: NotificationRepository,
) {
    operator fun invoke() = repository.markNotificationBannerDismissed()
}

class GetNotificationSettings(
    private val repository: NotificationRepository,
) {
    operator fun invoke() = NotificationSettings(
        isRemindersEnabled = repository.isRemindersEnabled(),
        isDailyExpenseLogEnabled = repository.isReminderEnabled(ReminderType.DAILY_EXPENSE_LOG),
        isWeeklySummaryEnabled = repository.isReminderEnabled(ReminderType.WEEKLY_SUMMARY),
        isIdleReEngagementEnabled = repository.isReminderEnabled(ReminderType.IDLE_RE_ENGAGEMENT),
        isMonthlyRecapEnabled = repository.isReminderEnabled(ReminderType.MONTHLY_RECAP),
        dailyReminderMinutes = repository.getDailyReminderMinutes(),
    )
}

class UpdateNotificationSettings(
    private val repository: NotificationRepository,
) {
    operator fun invoke(settings: NotificationSettings) {
        repository.setRemindersEnabled(settings.isRemindersEnabled)
        repository.setReminderEnabled(ReminderType.DAILY_EXPENSE_LOG, settings.isDailyExpenseLogEnabled)
        repository.setReminderEnabled(ReminderType.WEEKLY_SUMMARY, settings.isWeeklySummaryEnabled)
        repository.setReminderEnabled(ReminderType.IDLE_RE_ENGAGEMENT, settings.isIdleReEngagementEnabled)
        repository.setReminderEnabled(ReminderType.MONTHLY_RECAP, settings.isMonthlyRecapEnabled)
        repository.setDailyReminderMinutes(settings.dailyReminderMinutes)
    }
}

class RecordAppOpen(
    private val repository: NotificationRepository,
) {
    operator fun invoke() {
        repository.incrementAppOpenCount()
        if (repository.getFirstOpenTimestamp() == 0L) {
            repository.setFirstOpenTimestamp(Clock.System.now().toEpochMilliseconds())
        }
        val todayEpochDay = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toEpochDays()
        repository.setLastAppOpenEpochDay(todayEpochDay.toInt())
        repository.clearIdleStreakNudge()
    }
}

class RecordReminderFired(
    private val repository: NotificationRepository,
) {
    operator fun invoke(type: ReminderType) {
        repository.setLastReminderFiredAt(type, Clock.System.now().toEpochMilliseconds())
        if (type == ReminderType.IDLE_RE_ENGAGEMENT) {
            repository.setIdleStreakNudgeSent(true)
        }
    }
}

@Suppress("CyclomaticComplexMethod")
class ShouldFireReminder(
    private val repository: NotificationRepository,
) {
    operator fun invoke(type: ReminderType): Boolean {
        if (!repository.isRemindersEnabled() || !repository.isReminderEnabled(type)) return false
        return isDue(type)
    }

    private fun isDue(type: ReminderType): Boolean {
        val tz = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val today = now.toLocalDateTime(tz)
        val todayEpochDay = today.date.toEpochDays().toInt()
        return when (type) {
            ReminderType.DAILY_EXPENSE_LOG -> isDailyDue(today, todayEpochDay, tz)
            ReminderType.WEEKLY_SUMMARY -> isWeeklyDue(today, tz)
            ReminderType.IDLE_RE_ENGAGEMENT -> isIdleDue(todayEpochDay)
            ReminderType.MONTHLY_RECAP -> isMonthlyDue(today, tz)
        }
    }

    private fun isDailyDue(today: LocalDateTime, todayEpochDay: Int, tz: TimeZone): Boolean {
        val dailyHour = repository.getDailyReminderMinutes() / 60
        val startOfToday = LocalDateTime(today.date, LocalTime(0, 0)).toInstant(tz).toEpochMilliseconds()
        return today.hour >= dailyHour &&
            repository.getLastAppOpenEpochDay() != todayEpochDay &&
            repository.getLastReminderFiredAt(ReminderType.DAILY_EXPENSE_LOG) < startOfToday
    }

    private fun isWeeklyDue(today: LocalDateTime, tz: TimeZone): Boolean {
        val daysToSunday = today.dayOfWeek.isoDayNumber % 7
        val sundayDate = today.date.minus(DatePeriod(days = daysToSunday))
        val startOfThisWeekSunday = LocalDateTime(sundayDate, LocalTime(0, 0)).toInstant(tz).toEpochMilliseconds()
        return today.dayOfWeek == DayOfWeek.SUNDAY &&
            today.hour >= 10 &&
            repository.getLastReminderFiredAt(ReminderType.WEEKLY_SUMMARY) < startOfThisWeekSunday
    }

    private fun isIdleDue(todayEpochDay: Int): Boolean =
        (todayEpochDay - repository.getLastAppOpenEpochDay()) >= 5 && !repository.getIdleStreakNudgeSent()

    private fun isMonthlyDue(today: LocalDateTime, tz: TimeZone): Boolean {
        val nextMonthFirst = if (today.date.monthNumber == 12) {
            LocalDate(today.date.year + 1, 1, 1)
        } else {
            LocalDate(today.date.year, today.date.monthNumber + 1, 1)
        }
        val lastDayOfMonth = nextMonthFirst.minus(DatePeriod(days = 1)).dayOfMonth
        val startOfThisMonth = LocalDateTime(
            LocalDate(today.date.year, today.date.monthNumber, 1),
            LocalTime(0, 0),
        ).toInstant(tz).toEpochMilliseconds()
        return today.date.dayOfMonth >= (lastDayOfMonth - 2) &&
            repository.getLastReminderFiredAt(ReminderType.MONTHLY_RECAP) < startOfThisMonth
    }
}
