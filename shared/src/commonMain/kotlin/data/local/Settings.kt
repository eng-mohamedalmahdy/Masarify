package data.local

import com.russhwolf.settings.Settings


fun Settings.setIsFirstTime(isFirstTime: Boolean) {
    putBoolean("IS_FIRST_TIME", isFirstTime)
}

fun Settings.isFirstTime(): Boolean {
    return getBoolean("IS_FIRST_TIME", true)
}

fun Settings.isDarkModeEnabled(): Boolean {
    return getBoolean("DARK_MODE", true)
}

fun Settings.setDarkMode(isDarkMode: Boolean) {
    putBoolean("DARK_MODE", isDarkMode)
}