package com.lightfeather.designsystem.theme

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun rememberAppWindowSizeClass(): WindowWidthSizeClass {
    if (LocalInspectionMode.current) {
        return WindowWidthSizeClass.Compact
    }
    val activity = LocalContext.current.getActivity()
    val windowSize = calculateWindowSizeClass(activity!!)
    return windowSize.widthSizeClass
}

fun Context.getActivity(): ComponentActivity? =
    when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
