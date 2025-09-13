package com.lightfeather.designsystem.component.molecules.snackbar

import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object SnackbarService {
    // kotlinx.coroutines.channels.Channel.CHANNEL_DEFAULT_CAPACITY
    private const val CHANNEL_DEFAULT_CAPACITY = 64

    private val messageFlow =
        MutableSharedFlow<SnackbarMessage>(
            extraBufferCapacity = CHANNEL_DEFAULT_CAPACITY,
        )

    fun getSnackBarMessageFlow() =
        flow {
            val count = messageFlow.subscriptionCount.value + 1
            // only emit the snackbar message event to the last subscriber
            emitAll(messageFlow.filter { messageFlow.subscriptionCount.value == count })
        }

    fun sendSuccessMessage(text: String) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringTextMessage(text = text),
            ),
        )
    }

    fun sendSuccessMessage(textRes: StringResource) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringResTextMessage(textRes = textRes),
            ),
        )
    }

    fun sendWarningMessage(text: String) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringTextMessage(text = text),
                type = SnackbarType.WARNING,
            ),
        )
    }

    fun sendWarningMessage(textRes: StringResource) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringResTextMessage(textRes = textRes),
                type = SnackbarType.WARNING,
            ),
        )
    }

    fun sendErrorMessage(text: String) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringTextMessage(text = text),
                type = SnackbarType.ERROR,
            ),
        )
    }

    fun sendErrorMessage(textRes: StringResource) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = StringResTextMessage(textRes = textRes),
                type = SnackbarType.ERROR,
            ),
        )
    }

    fun sendErrorMessage(textMessage: TextMessage) {
        messageFlow.tryEmit(
            SnackbarMessage(
                textMessage = textMessage,
                type = SnackbarType.ERROR,
            ),
        )
    }

    fun sendMessage(message: SnackbarMessage) {
        messageFlow.tryEmit(message)
    }

    fun sendErrorMessageOrUnknown(message: String?) {
//        if(message.isNullOrEmpty()){
//            sendErrorMessage(Res.strings.unknown_error)
//        }else{
//            sendErrorMessage(message)
//        }
        sendErrorMessage(message.orEmpty())
    }
}

fun CoroutineScope.handleSnackbarMessages(snackbarHostState: AppSnackbarHostState) {
    SnackbarService
        .getSnackBarMessageFlow()
        .onEach { message ->
            snackbarHostState.showSnackbar(message)
        }.launchIn(this)
}
