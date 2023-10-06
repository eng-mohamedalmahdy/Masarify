package ui.composeables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ui.style.cardColor

@Composable
fun LineOnlyTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    readonlyAndDisabled: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = TextFieldDefaults.textFieldColors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        backgroundColor = cardColor
    )


    TextField(
        shape = RoundedCornerShape(size = 9.dp),
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.body1,
        singleLine = true,
        modifier = modifier.padding(4.dp),
        colors = colors,
        label = { label() },
        value = text,
        readOnly = readonlyAndDisabled,
        enabled = !readonlyAndDisabled
    )
}
