package ext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import ui.entity.UiState

fun String.toComposeColor() = Color(removePrefix("#").toLong(16) or 0x00000000FF000000)
fun Color.toHexString(): String {
    // Convert each color component to its hex representation
    val red = (this.red * 255).toInt().toString(16).padStart(2, '0')
    val green = (this.green * 255).toInt().toString(16).padStart(2, '0')
    val blue = (this.blue * 255).toInt().toString(16).padStart(2, '0')
    val alpha = (this.alpha * 255).toInt().toString(16).padStart(2, '0')

    // Combine them to form the hex string
    return "#$alpha$red$green$blue"
}

fun Color.generateBackgroundColor(darkeningFactor: Float): Color {
    val luminance = luminance() * darkeningFactor

    val red = (red * luminance).coerceIn(0f, 1f)
    val green = (green * luminance).coerceIn(0f, 1f)
    val blue = (blue * luminance).coerceIn(0f, 1f)

    return Color(red, green, blue)
}


@Composable
fun <T> OnUiStateChange(
    uiState: UiState<T>,
    onIdle: @Composable () -> Unit = {},
    onLoading: @Composable () -> Unit = { Box(Modifier.fillMaxSize()) { CircularProgressIndicator(color = MaterialTheme.colors.primary) } },
    onError: @Composable (Throwable) -> Unit = {},
    onSuccess: @Composable (T) -> Unit,

    ) {
    when (uiState) {
        is UiState.LOADING -> onLoading()
        is UiState.IDLE<T> -> onIdle()
        is UiState.SUCCESS -> onSuccess(uiState.data)
        is UiState.ERROR -> onError(uiState.throwable)
    }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }