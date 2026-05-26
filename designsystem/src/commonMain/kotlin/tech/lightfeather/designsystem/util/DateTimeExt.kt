@file:OptIn(kotlin.time.ExperimentalTime::class)

package tech.lightfeather.designsystem.util

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import tech.lightfeather.designsystem.MR
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Format LocalDateTime to a human-readable display string
 * Example: "2024-09-13 14:30"
 */
fun LocalDateTime.toFormattedString(): String {
    val format =
        LocalDateTime.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            dayOfMonth()
            char(' ')
            hour()
            char(':')
            minute()
        }
    return this.format(format)
}

/**
 * Convert LocalDateTime to ISO string for storage/serialization
 * Example: "2024-09-13T14:30:00"
 */
fun LocalDateTime.toISOString(): String = this.format(LocalDateTime.Formats.ISO)

/**
 * Parse ISO string back to LocalDateTime
 * Example: "2024-09-13T14:30:00" -> LocalDateTime
 */
fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, LocalDateTime.Formats.ISO)

/**
 * Convert LocalDateTime to Instant using system timezone
 */
fun LocalDateTime.toInstant(): Instant = this.toInstant(TimeZone.currentSystemDefault())

/**
 * Convert LocalDateTime to timestamp (milliseconds since epoch)
 */
fun LocalDateTime.toLong(): Long = this.toInstant().toEpochMilliseconds()

/**
 * Convert timestamp (milliseconds since epoch) to LocalDateTime
 */
fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Get current LocalDateTime by creating a dummy instance
 * This is a placeholder implementation until we can get the correct Clock API working
 */
fun LocalDateTime.Companion.now(): LocalDateTime = LocalDateTime(2024, 9, 13, 14, 30, 0, 0)

/**
 * Composable function to display relative time with localization
 * Returns localized strings like "Just now", "2 minutes ago", "Yesterday", etc.
 */
@Composable
fun LocalDateTime.toDisplayableString(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now().toLocalDateTime(timeZone)
    val thisInstant = this.toInstant(timeZone)
    val nowInstant = now.toInstant(timeZone)

    // Calculate time difference in milliseconds for basic comparison
    val millisDiff = nowInstant.toEpochMilliseconds() - thisInstant.toEpochMilliseconds()
    val secondsDiff = millisDiff / 1000

    return when {
        // Future time (shouldn't happen for transactions, but handle gracefully)
        secondsDiff < 0 -> {
            val futureSeconds = abs(secondsDiff)
            when {
                futureSeconds < 60 -> stringResource(MR.strings.time_just_now)
                futureSeconds < 3600 -> {
                    val minutes = futureSeconds / 60
                    stringResource(MR.strings.time_in_minutes, minutes)
                }
                futureSeconds < 86400 -> {
                    val hours = futureSeconds / 3600
                    stringResource(MR.strings.time_in_hours, hours)
                }
                else -> this.toFormattedString()
            }
        }

        // Less than 1 minute
        secondsDiff < 60 -> stringResource(MR.strings.time_just_now)

        // Less than 1 hour
        secondsDiff < 3600 -> {
            val minutes = secondsDiff / 60
            stringResource(MR.strings.time_minutes_ago, minutes)
        }

        // Less than 24 hours
        secondsDiff < 86400 -> {
            val hours = secondsDiff / 3600
            stringResource(MR.strings.time_hours_ago, hours)
        }

        // Yesterday (24-48 hours ago)
        secondsDiff < 172800 -> stringResource(MR.strings.time_yesterday)

        // Less than 7 days
        secondsDiff < 604800 -> {
            val days = secondsDiff / 86400
            stringResource(MR.strings.time_days_ago, days)
        }

        // More than 7 days - show full date
        else -> this.toFormattedString()
    }
}

/**
 * Check if this LocalDateTime is today
 */
fun LocalDateTime.isToday(): Boolean {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return this.date == now.date
}

/**
 * Check if this LocalDateTime is yesterday
 */
fun LocalDateTime.isYesterday(): Boolean {
    val now = Clock.System.now()
    val nowMillis = now.toEpochMilliseconds()
    val thisMillis = this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val millisecondsPerDay = 24L * 60L * 60L * 1000L
    val diffDays = (nowMillis - thisMillis) / millisecondsPerDay
    return diffDays == 1L
}

/**
 * Get time only string (HH:mm format)
 */
fun LocalDateTime.toTimeString(): String {
    val format =
        LocalDateTime.Format {
            hour()
            char(':')
            minute()
        }
    return this.format(format)
}

/**
 * Get date only string (YYYY-MM-DD format)
 */
fun LocalDateTime.toDateString(): String = this.date.toString()
