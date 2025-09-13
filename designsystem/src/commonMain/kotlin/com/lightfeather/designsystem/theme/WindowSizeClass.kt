package com.lightfeather.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
expect fun rememberAppWindowSizeClass(): WindowWidthSizeClass

@Composable
fun responsiveTypography(): Typography {
    val windowSize = rememberAppWindowSizeClass()
    val base = AppTheme.typography

    return when (windowSize) {
        WindowWidthSizeClass.Compact ->
            base.copy(
                displayLarge = base.displayLarge.copy(fontSize = 45.sp, lineHeight = 48.sp),
                displayMedium = base.displayMedium.copy(fontSize = 36.sp, lineHeight = 40.sp),
                displaySmall = base.displaySmall.copy(fontSize = 28.sp, lineHeight = 32.sp),
                headlineLarge = base.headlineLarge.copy(fontSize = 24.sp, lineHeight = 26.sp),
                headlineMedium = base.headlineMedium.copy(fontSize = 20.sp, lineHeight = 22.sp),
                headlineSmall = base.headlineSmall.copy(fontSize = 18.sp, lineHeight = 20.sp),
                bodyLarge = base.bodyLarge.copy(fontSize = 14.sp, lineHeight = 16.sp),
                bodyMedium = base.bodyMedium.copy(fontSize = 12.sp, lineHeight = 14.sp),
                bodySmall = base.bodySmall.copy(fontSize = 11.sp, lineHeight = 13.sp),
                labelLarge = base.labelLarge.copy(fontSize = 12.sp, lineHeight = 14.sp),
                labelMedium = base.labelMedium.copy(fontSize = 11.sp, lineHeight = 13.sp),
                labelSmall = base.labelSmall.copy(fontSize = 10.sp, lineHeight = 11.sp),
            )
        WindowWidthSizeClass.Medium ->
            base.copy(
                displayLarge = base.displayLarge.copy(fontSize = 52.sp, lineHeight = 56.sp),
                displayMedium = base.displayMedium.copy(fontSize = 40.sp, lineHeight = 44.sp),
                displaySmall = base.displaySmall.copy(fontSize = 32.sp, lineHeight = 36.sp),
                headlineLarge = base.headlineLarge.copy(fontSize = 28.sp, lineHeight = 30.sp),
                headlineMedium = base.headlineMedium.copy(fontSize = 24.sp, lineHeight = 26.sp),
                headlineSmall = base.headlineSmall.copy(fontSize = 20.sp, lineHeight = 22.sp),
                bodyLarge = base.bodyLarge.copy(fontSize = 15.sp, lineHeight = 18.sp),
                bodyMedium = base.bodyMedium.copy(fontSize = 13.sp, lineHeight = 15.sp),
                bodySmall = base.bodySmall.copy(fontSize = 12.sp, lineHeight = 14.sp),
                labelLarge = base.labelLarge.copy(fontSize = 13.sp, lineHeight = 15.sp),
                labelMedium = base.labelMedium.copy(fontSize = 12.sp, lineHeight = 14.sp),
                labelSmall = base.labelSmall.copy(fontSize = 11.sp, lineHeight = 13.sp),
            )
        WindowWidthSizeClass.Expanded -> base
        else -> base
    }
}
