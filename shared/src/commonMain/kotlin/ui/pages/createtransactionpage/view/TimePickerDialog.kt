package ui.pages.createtransactionpage.view

import ui.main.LocalAppTheme
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
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import rememberPickerState
import ui.composeables.TimePicker

@Composable
fun TimePickerDialog(
    initialValue: Triple<String, String, String>,
    onConfirmClick: (Triple<String, String, String>) -> Unit,
    onCancelClick: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val hours = rememberPickerState(initialValue.first)
    val mins = rememberPickerState(initialValue.second)
    val ampm = rememberPickerState(initialValue.third)

    Dialog(onDismissRequest = { onDismissDialog() }) {
        Card(
            Modifier.size(280.dp, 260.dp)
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
                            color = LocalAppTheme.current.grayColor,
                            shape = RoundedCornerShape(size = 0.dp)
                        )
                    ) {
                        Text(
                            stringResource(MR.strings.cancel),
                            color = LocalAppTheme.current.grayColor
                        )
                    }
                    TextButton(
                        onClick = {

                            onConfirmClick(
                                Triple(
                                    hours.selectedItem.padStart(2, '0'),
                                    mins.selectedItem.padStart(2, '0') + ":00",
                                    " ${ampm.selectedItem}"
                                )

                            )
                        },
                        modifier = Modifier.weight(1f).border(
                            width = 1.dp,
                            color = LocalAppTheme.current.grayColor,
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