package com.lightfeather.designsystem.component.molecules.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Immutable
sealed class TextMessage

@Immutable
data class StringTextMessage(
    val text: String,
) : TextMessage()

@Immutable
data class StringResTextMessage(
    val textRes: StringResource,
    val formatArgs: Any? = null,
) : TextMessage()

@Composable
fun TextMessage.getText(): String =
    when (this) {
        is StringTextMessage -> text
        is StringResTextMessage ->
            if (formatArgs != null) {
                stringResource(textRes, formatArgs)
            } else {
                stringResource(textRes)
            }
    }
