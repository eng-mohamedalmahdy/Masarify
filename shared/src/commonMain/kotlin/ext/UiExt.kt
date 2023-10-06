package ext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ui.entity.UiState

fun String.colorFromHex() = Color(removePrefix("#").toLong(16) or 0x00000000FF000000)

@Composable
fun <T> UiState<T>.OnUiStateChange(
    onIdle: @Composable () -> Unit = {},
    onLoading: @Composable () -> Unit = { Box(Modifier.fillMaxSize()) { CircularProgressIndicator(color = MaterialTheme.colors.primary) } },
    onError: @Composable (Throwable) -> Unit = {},
    onSuccess: @Composable (T) -> Unit,

    ) {
    when (this) {
        UiState.LOADING -> onLoading()
        is UiState.IDLE<T> -> onIdle()
        is UiState.SUCCESS -> onSuccess(data)
        is UiState.ERROR -> onError(throwable)
    }
}