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
                displayLarge = base.displayLarge.copy(fontSize = 45.sp, lineHeight = 52.sp),
                displayMedium = base.displayMedium.copy(fontSize = 36.sp, lineHeight = 44.sp),
                displaySmall = base.displaySmall.copy(fontSize = 28.sp, lineHeight = 36.sp),
                headlineLarge = base.headlineLarge.copy(fontSize = 24.sp, lineHeight = 32.sp),
                headlineMedium = base.headlineMedium.copy(fontSize = 20.sp, lineHeight = 28.sp),
                headlineSmall = base.headlineSmall.copy(fontSize = 18.sp, lineHeight = 24.sp),
                bodyLarge = base.bodyLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
                bodyMedium = base.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
                bodySmall = base.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                labelLarge = base.labelLarge.copy(fontSize = 12.sp, lineHeight = 16.sp),
                labelMedium = base.labelMedium.copy(fontSize = 11.sp, lineHeight = 14.sp),
                labelSmall = base.labelSmall.copy(fontSize = 10.sp, lineHeight = 12.sp),
            )
        WindowWidthSizeClass.Medium ->
            base.copy(
                displayLarge = base.displayLarge.copy(fontSize = 52.sp, lineHeight = 60.sp),
                displayMedium = base.displayMedium.copy(fontSize = 40.sp, lineHeight = 48.sp),
                displaySmall = base.displaySmall.copy(fontSize = 32.sp, lineHeight = 40.sp),
                headlineLarge = base.headlineLarge.copy(fontSize = 28.sp, lineHeight = 36.sp),
                headlineMedium = base.headlineMedium.copy(fontSize = 24.sp, lineHeight = 32.sp),
                headlineSmall = base.headlineSmall.copy(fontSize = 20.sp, lineHeight = 28.sp),
                bodyLarge = base.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                bodyMedium = base.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                bodySmall = base.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                labelLarge = base.labelLarge.copy(fontSize = 13.sp, lineHeight = 18.sp),
                labelMedium = base.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
                labelSmall = base.labelSmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
            )
        WindowWidthSizeClass.Expanded -> base
        else -> base
    }
}
