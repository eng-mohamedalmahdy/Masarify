package tech.lightfeather.masarify.util

import kotlin.math.roundToLong

/**
 * Formats a Double value to a string with 2 decimal places
 * KMP-compatible alternative to String.format()
 *
 * @param value The numeric value to format
 * @return Formatted string with 2 decimal places (e.g., "123.45")
 */
fun formatAmount(value: Double): String {
    val rounded = (value * 100).roundToLong() / 100.0
    val integerPart = rounded.toLong()
    val decimalPart = ((rounded - integerPart) * 100).roundToLong().toString().padStart(2, '0')
    return "$integerPart.$decimalPart"
}
