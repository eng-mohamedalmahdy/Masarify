package com.lightfeather.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * JVM implementation for DynamicColorScheme.
 * JVM platforms don't support dynamic color schemes, so we return the default light color scheme.
 */
actual fun createDynamicColorScheme(isDarkTheme: Boolean): ColorScheme {
    // JVM platforms don't support dynamic colors yet
    return lightColorScheme()
}
