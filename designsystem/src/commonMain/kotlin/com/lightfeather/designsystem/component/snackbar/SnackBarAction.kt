package com.lightfeather.designsystem.component.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lightfeather.designsystem.theme.AppTheme

sealed class SnackBarAction(
    val name: String,
) {
    operator fun invoke() = name

    data object Success : SnackBarAction("success")

    data object Error : SnackBarAction("error")

    data object Info : SnackBarAction("info")
}

val SnackBarAction.color: Color
    @Composable get() =
        when (this) {
            SnackBarAction.Error -> AppTheme.colors.error
            SnackBarAction.Info -> AppTheme.colors.backgroundDark
            SnackBarAction.Success -> AppTheme.colors.success
        }

fun String.asSnakeBarAction(): SnackBarAction? =
    when (this) {
        SnackBarAction.Success() -> SnackBarAction.Success
        SnackBarAction.Error() -> SnackBarAction.Error
        SnackBarAction.Info() -> SnackBarAction.Info
        else -> null
    }

fun getSnackbarBackgroundColor(type: SnackbarType): Color =
    when (type) {
        SnackbarType.ERROR -> AppTheme.colors.error
        SnackbarType.WARNING -> AppTheme.colors.warning
        SnackbarType.SUCCESS -> AppTheme.colors.success
        SnackbarType.INFO -> AppTheme.colors.primary
    }

fun getSnackbarTextColor(type: SnackbarType): Color =
    when (type) {
        SnackbarType.ERROR -> AppTheme.colors.surfaceLight
        SnackbarType.WARNING -> AppTheme.colors.surfaceLight
        SnackbarType.SUCCESS -> AppTheme.colors.surfaceLight
        SnackbarType.INFO -> AppTheme.colors.surfaceLight
    }
