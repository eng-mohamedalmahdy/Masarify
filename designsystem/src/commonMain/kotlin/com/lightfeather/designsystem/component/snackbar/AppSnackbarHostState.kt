package com.lightfeather.happytail.designsystem.snackbar

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@Stable
class AppSnackbarHostState {
    private val mutex = Mutex()

    var currentSnackbarData by mutableStateOf<SnackbarMessageData?>(null)
        private set

    suspend fun showSnackbar(
        message: SnackbarMessage
    ): SnackbarResult = mutex.withLock {
        try {
            return suspendCancellableCoroutine { continuation ->
                currentSnackbarData = SnackbarMessageData(message, continuation)
            }
        } finally {
            currentSnackbarData = null
        }
    }
}

@Stable
class SnackbarMessageData(
    val message: SnackbarMessage,
    private val continuation: CancellableContinuation<SnackbarResult>
) {

    fun performAction() {
        if (continuation.isActive) {
            continuation.resume(SnackbarResult.ActionPerformed)
        }
    }

    fun dismiss() {
        if (continuation.isActive) {
            continuation.resume(SnackbarResult.Dismissed)
        }
    }
}