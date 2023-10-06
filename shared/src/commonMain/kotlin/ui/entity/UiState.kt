package ui.entity

sealed class UiState<T> {
    class IDLE<T>() : UiState<T>()

    data object LOADING : UiState<Nothing>()
    data class SUCCESS<T>(val data: T) : UiState<T>()

    data class ERROR<T>(val throwable: Throwable) : UiState<T>()
}