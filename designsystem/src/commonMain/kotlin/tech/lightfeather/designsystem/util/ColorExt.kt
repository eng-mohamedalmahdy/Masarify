package tech.lightfeather.designsystem.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

fun String.toColorInt(): Int {
    require(length == 7 || length == 9) { "Color should be in #RRGGBB or #RRGGBBAA format" }
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

fun parseColor(colorString: String): Color {
    val hex = colorString.removePrefix("#")
    return when (hex.length) {
        6 -> { // RGB
            val r = hex.substring(0, 2).toInt(16) / 255f
            val g = hex.substring(2, 4).toInt(16) / 255f
            val b = hex.substring(4, 6).toInt(16) / 255f
            Color(r, g, b, 1f)
        }

        8 -> { // ARGB
            val a = hex.substring(0, 2).toInt(16) / 255f
            val r = hex.substring(2, 4).toInt(16) / 255f
            val g = hex.substring(4, 6).toInt(16) / 255f
            val b = hex.substring(6, 8).toInt(16) / 255f
            Color(r, g, b, a)
        }

        else -> Color.Gray
    }
}

fun colorToHex(color: Color): String {
    val r = (color.red * 255).roundToInt()
    val g = (color.green * 255).roundToInt()
    val b = (color.blue * 255).roundToInt()

    return buildString {
        append("#")
        append(r.toString(16).padStart(2, '0').uppercase())
        append(g.toString(16).padStart(2, '0').uppercase())
        append(b.toString(16).padStart(2, '0').uppercase())
    }
}
