package com.lightfeather.designsystem.util

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
