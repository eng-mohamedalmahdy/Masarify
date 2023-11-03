package ui.composeables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ui.style.AppTheme.cardColor
import androidx.compose.material3.TextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun LineOnlyTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    readonlyAndDisabled: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val colors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = cardColor,
        unfocusedContainerColor = cardColor,
        disabledContainerColor = cardColor,
    )


    TextField(
        value = text,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(size = 9.dp),
        colors = colors,
        modifier = modifier.padding(4.dp),
        label = label,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = keyboardType),
         singleLine = true,
         readOnly = readonlyAndDisabled,
         enabled = !readonlyAndDisabled
    )
}
