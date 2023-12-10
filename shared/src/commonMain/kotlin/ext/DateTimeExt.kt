package ext

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.formatTimeStampToTime(timeZone: TimeZone = TimeZone.UTC): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(timeZone)
    val time = localDateTime.time

    val hour = (if (time.hour > 12) time.hour % 12 else time.hour).toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    val second = time.second.toString().padStart(2, '0')
    val amPm = if (time.hour <= 12) "am" else "pm"
    return "$hour:${minute}:${second} $amPm"
}

fun String.getHoursFromTimeFormatted(): String {
    val parts = this.split(" ")
    val timePart = parts[0]
    val timeComponents = timePart.split(":")
    return timeComponents[0].padStart(2, '0')
}

fun String.getMinutesAndSecondsFromTimeFormatted(): String {
    val parts = this.split(" ")
    val timePart = parts[0]
    val timeComponents = timePart.split(":")
    return timeComponents[1].padStart(2, '0')+":"+timeComponents[2].padStart(2, '0')
}
fun String.getAmPmFromTimeFormatted(): String {
    val parts = this.split(" ")
    return parts[1]
}

fun Long.formatTimeStampToDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
    return localDateTime.date.toString()
}

fun String.dateToTimestamp(): Long {
    return Instant.parse(this).toEpochMilliseconds()
}

fun String.convertTo24HourFormat(): String {
    val parts = this.split(" ")
    val timePart = parts[0]
    val amPmPart = parts[1]

    val timeComponents = timePart.split(":")
    var hour = timeComponents[0].toInt()

    // Adjust hour based on AM/PM
    if (amPmPart.equals("PM", ignoreCase = true) && hour != 12) {
        hour += 12
    } else if (amPmPart.equals("AM", ignoreCase = true) && hour == 12) {
        hour = 0
    }

    val hourString = hour.toString().padStart(2, '0')
    val minuteSecondPart = timeComponents.subList(1, timeComponents.size).joinToString(":")

    return "$hourString:$minuteSecondPart"
}