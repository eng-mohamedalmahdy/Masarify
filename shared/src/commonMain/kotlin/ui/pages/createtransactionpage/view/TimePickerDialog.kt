package ui.pages.createtransactionpage.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.MR.strings.time
import dev.icerock.moko.resources.compose.stringResource
import rememberPickerState
import ui.composeables.TimePicker
import ui.style.grayColor

@Composable
fun TimePickerDialog(onConfirmClick: (String) -> Unit, onCancelClick: () -> Unit, onDismissDialog: () -> Unit) {
    var hours = rememberPickerState()
    var mins = rememberPickerState()
    var ampm = rememberPickerState()

    Dialog(onDismissRequest = { onDismissDialog() }) {
        Card(
            Modifier.size(280.dp, 200.dp)
                .border(20.dp, color = Color(0xFF97BEAF), shape = RoundedCornerShape(size = 20.dp))
                .background(Color.White, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(size = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxWidth().padding(horizontal = 30.dp).padding(top = 40.dp)) {
                    TimePicker(hours, mins, ampm)
                }

                Row(Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(
                        onClick = { onCancelClick() },
                        modifier = Modifier.weight(1f).border(
                            width = 1.dp,
                            color = grayColor,
                            shape = RoundedCornerShape(size = 0.dp)
                        )
                    ) {
                        Text(
                            stringResource(MR.strings.cancel),
                            color = grayColor
                        )
                    }
                    TextButton(
                        onClick = {

                            onConfirmClick("${hours.selectedItem}:${mins.selectedItem} ${ampm.selectedItem}")
                        },
                        modifier = Modifier.weight(1f).border(
                            width = 1.dp,
                            color = grayColor,
                            shape = RoundedCornerShape(size = 0.dp)
                        )
                    ) {
                        Text(stringResource(MR.strings.accept), color = Color.Black)
                    }
                }
            }
        }
    }
}