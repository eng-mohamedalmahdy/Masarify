package ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed class SnackBarAction(val name: String) {
    operator fun invoke() = name

    data object Success : SnackBarAction("success")

    data object Error : SnackBarAction("error")

    data object Info : SnackBarAction("info")
}

val SnackBarAction.color: Color
    @Composable get() = when (this) {
        SnackBarAction.Error -> MaterialTheme.colorScheme.error
        SnackBarAction.Info -> MaterialTheme.colorScheme.onBackground
        SnackBarAction.Success -> Color(0xFFFFA500) // Orange color
    }

fun String.asSnakeBarAction(): SnackBarAction? = when (this) {
    SnackBarAction.Success() -> SnackBarAction.Success
    SnackBarAction.Error() -> SnackBarAction.Error
    SnackBarAction.Info() -> SnackBarAction.Info
    else -> null
}