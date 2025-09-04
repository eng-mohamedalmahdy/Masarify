package com.lightfeather.designsystem.theme
//
//import androidx.compose.material3.ColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import com.materialkolor.toColorScheme
//import kotlinx.browser.document
//import kotlinx.browser.window
//
//
//@Composable
//actual fun dynamicColorScheme(
//    isDark: Boolean,
//    fallback: ColorScheme
//): ColorScheme {
//    val seed = getCssAccentColor()?.let { parseCssColor(it) }
//    return if (seed != null) rememberAppColorScheme(
//        seedColor = seed,
//        isDark = window.matchMedia("(prefers-color-scheme: dark)").matches,
//        fallback = fallback
//    ).toColorScheme()
//    else fallback
//}
//
//fun getCssAccentColor(): String? {
//    val style = document.documentElement?.let { window.getComputedStyle(it) }
//    return style?.getPropertyValue("accent-color")?.takeIf { it.isNotBlank() }
//}
//
//fun parseCssColor(css: String): Color {
//    val value = css.trim().lowercase()
//
//    // HEX (#rrggbb or #rgb)
//    if (value.startsWith("#")) {
//        val hex = value.removePrefix("#")
//        return when (hex.length) {
//            3 -> {
//                val r = hex[0].digitToInt(16) * 17
//                val g = hex[1].digitToInt(16) * 17
//                val b = hex[2].digitToInt(16) * 17
//                Color(r, g, b)
//            }
//
//            6 -> {
//                val r = hex.substring(0, 2).toInt(16)
//                val g = hex.substring(2, 4).toInt(16)
//                val b = hex.substring(4, 6).toInt(16)
//                Color(r, g, b)
//            }
//
//            8 -> { // #rrggbbaa
//                val r = hex.substring(0, 2).toInt(16)
//                val g = hex.substring(2, 4).toInt(16)
//                val b = hex.substring(4, 6).toInt(16)
//                val a = hex.substring(6, 8).toInt(16)
//                Color(r, g, b, a)
//            }
//
//            else -> Color.Unspecified
//        }
//    }
//
//    // rgb() or rgba()
//    if (value.startsWith("rgb")) {
//        val nums = value.substringAfter("(").substringBefore(")")
//            .split(",").map { it.trim() }
//        val r = nums[0].toInt()
//        val g = nums[1].toInt()
//        val b = nums[2].toInt()
//        val a = if (nums.size > 3) (nums[3].toFloat() * 255).toInt() else 255
//        return Color(r, g, b, a)
//    }
//
//    // Named colors (basic fallback)
//    return when (value) {
//        "black" -> Color.Black
//        "white" -> Color.White
//        "red" -> Color.Red
//        "green" -> Color.Green
//        "blue" -> Color.Blue
//        "yellow" -> Color.Yellow
//        "cyan" -> Color.Cyan
//        "magenta" -> Color.Magenta
//        else -> Color.Unspecified
//    }
//}
