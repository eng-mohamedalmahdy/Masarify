package ext

import androidx.compose.ui.graphics.Color

fun String.colorFromHex() = Color(removePrefix("#").toLong(16) or 0x00000000FF000000)

