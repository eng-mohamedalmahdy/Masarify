package com.lightfeather.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicColorScheme(
    isDark: Boolean,
    fallback: ColorScheme
): ColorScheme {
    return fallback
}