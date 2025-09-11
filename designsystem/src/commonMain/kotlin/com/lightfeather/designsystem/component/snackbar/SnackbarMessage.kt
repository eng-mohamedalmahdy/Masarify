package com.lightfeather.designsystem.component.snackbar

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource

@Immutable
data class SnackbarMessage(
    val textMessage: TextMessage,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val type: SnackbarType = SnackbarType.SUCCESS,
    val drawableResource: DrawableResource? = null,
    val autoDismiss: Boolean = true,
)

enum class SnackbarType {
    ERROR,
    WARNING,
    SUCCESS,
    INFO,
}
