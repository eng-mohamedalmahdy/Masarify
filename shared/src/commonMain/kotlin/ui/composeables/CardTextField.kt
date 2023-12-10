package ui.composeables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ui.main.LocalAppTheme

@Composable
fun CardTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    readonlyAndDisabled: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = TextFieldDefaults.colors(
        disabledTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = LocalAppTheme.current.cardColor,
        unfocusedContainerColor = LocalAppTheme.current.cardColor,
        disabledContainerColor = LocalAppTheme.current.cardColor,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,

    )
) {

    runCatching {

        TextField(
            value = text,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(size = 9.dp),
            colors = colors,
            modifier = modifier.padding(4.dp),
            label = label,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType, imeAction = ImeAction.Done),
            singleLine = true,
            readOnly = readonlyAndDisabled,
            enabled = !readonlyAndDisabled,
            trailingIcon = { trailingIcon?.let { Icon(it, null, tint = MaterialTheme.colorScheme.onBackground) } }
        )
    }
}
