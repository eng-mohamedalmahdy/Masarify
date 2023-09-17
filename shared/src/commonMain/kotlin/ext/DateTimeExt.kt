package ext

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.formatTimeStampToTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.time.toString().substringBeforeLast(":")
}

fun Long.formatTimeStampToDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.date.toString()
}