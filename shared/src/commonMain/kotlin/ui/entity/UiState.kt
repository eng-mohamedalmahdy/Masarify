package ui.entity

sealed class UiState<T> {
    class IDLE<T>() : UiState<T>()

    class LOADING<T> : UiState<T>()
    data class SUCCESS<T>(val data: T) : UiState<T>()

    data class ERROR<T>(val throwable: Throwable) : UiState<T>()
}