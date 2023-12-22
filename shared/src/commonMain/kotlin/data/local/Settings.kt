package data.local

import com.russhwolf.settings.Settings


fun Settings.setIsFirstTime(isFirstTime: Boolean) {
    putBoolean("IS_FIRST_TIME", isFirstTime)
}

fun Settings.isFirstTime(): Boolean = getBoolean("IS_FIRST_TIME", true)


fun Settings.isDarkModeEnabled(): Boolean = getBoolean("DARK_MODE", true)


fun Settings.setDarkMode(isDarkMode: Boolean) {
    putBoolean("DARK_MODE", isDarkMode)
}

fun Settings.setLanguage(language: String) {
    putString("LANGUAGE", language)
}

fun Settings.getLanguage() = getString("LANGUAGE", "en")