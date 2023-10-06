package ui.composeables

import Picker
import PickerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import rememberPickerState


@Composable
fun TimePicker(
    hoursPickerState: PickerState,
    minutesPickerState: PickerState,
    amPmPickerState: PickerState
) {

    val hours = List(12) { "${it + 1}" }
    val minutes = List(60) { "$it" }
    val amPm = listOf(stringResource(MR.strings.am), stringResource(MR.strings.pm))


    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Picker(hours, hoursPickerState, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(4.dp))
        Picker(minutes, minutesPickerState, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Picker(amPm, amPmPickerState, modifier = Modifier.weight(1f))
    }
}
