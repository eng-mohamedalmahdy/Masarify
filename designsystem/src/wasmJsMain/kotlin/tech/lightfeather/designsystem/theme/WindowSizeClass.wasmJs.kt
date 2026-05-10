package tech.lightfeather.designsystem.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun rememberAppWindowSizeClass(): WindowWidthSizeClass {
    val windowSize = calculateWindowSizeClass()
    return windowSize.widthSizeClass
}
