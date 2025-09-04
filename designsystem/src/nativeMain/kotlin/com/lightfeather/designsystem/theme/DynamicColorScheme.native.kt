package com.lightfeather.designsystem.theme
//
//import androidx.compose.material3.ColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import com.materialkolor.toColorScheme
//import kotlinx.cinterop.ExperimentalForeignApi
//import kotlinx.cinterop.get
//import platform.CoreGraphics.CGColorGetComponents
//import platform.CoreGraphics.CGColorGetNumberOfComponents
//import platform.UIKit.UIColor
//import platform.UIKit.tintColor
//
//@Composable
//actual fun dynamicColorScheme(
//    isDark: Boolean,
//    fallback: ColorScheme
//): ColorScheme {
//    val tintColor = UIColor.tintColor.asComposeColor()
//    if (tintColor == null) return fallback
//    return rememberAppColorScheme(seedColor = tintColor, isDark = isDark, fallback).toColorScheme()
//}
//
//@OptIn(ExperimentalForeignApi::class)
//fun UIColor.asComposeColor(): Color? {
//    val cgColor = this.CGColor ?: return null
//    val components = CGColorGetComponents(cgColor) ?: return null
//    val count = CGColorGetNumberOfComponents(cgColor).toInt()
//
//    return when (count) {
//        2 -> { // grayscale + alpha
//            val gray = components[0].toFloat()
//            val alpha = components[1].toFloat()
//            Color(gray, gray, gray, alpha)
//        }
//
//        4 -> { // RGBA
//            Color(
//                red = components[0].toFloat(),
//                green = components[1].toFloat(),
//                blue = components[2].toFloat(),
//                alpha = components[3].toFloat()
//            )
//        }
//
//        else -> Color.Gray
//    }
//}