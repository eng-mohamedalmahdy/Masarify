package ui.composeables

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import ui.style.textFieldIndicatorColor

@Composable
fun LineOnlyTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    lineColor: Color = textFieldIndicatorColor,
    readonly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = TextFieldDefaults.textFieldColors(
        unfocusedIndicatorColor = lineColor,
        focusedIndicatorColor = lineColor,
        backgroundColor = Color.Transparent
    )


    TextField(
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.body1,
        singleLine = true,
        modifier = modifier.background(Color.Transparent),
        colors = colors,
        label = { label() },
        value = text,
        readOnly = readonly
    )
}
