package com.lightfeather.designsystem.component.snackbar

/**
 * Adapted from [androidx.compose.material3.SnackbarDuration]
 */
enum class SnackbarDuration {
    /**
     * Show the Snackbar for a short period of time
     */
    Short,

    /**
     * Show the Snackbar for a long period of time
     */
    Long,

    /**
     * Show the Snackbar indefinitely until explicitly dismissed or action is clicked
     */
    Indefinite
}
