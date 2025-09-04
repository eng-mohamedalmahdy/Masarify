package com.lightfeather.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Checkbox as MaterialCheckbox

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
        disabledCheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledIndeterminateColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    MaterialCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors
    )
}

@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
        disabledCheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledIndeterminateColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    Row(
        modifier = modifier
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // We handle changes through the row's clickable modifier
            enabled = enabled,
            colors = colors
        )
        Spacer(modifier = Modifier.width(8.dp))
        label()
    }
}

@Preview
@Composable
private fun PreviewCheckbox() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var checked1 by remember { mutableStateOf(false) }
            var checked2 by remember { mutableStateOf(true) }

            Checkbox(
                checked = checked1,
                onCheckedChange = { checked1 = it }
            )
            Checkbox(
                checked = checked2,
                onCheckedChange = { checked2 = it }
            )

            CheckboxWithLabel(
                checked = checked2,
                onCheckedChange = { checked2 = it },
                label = { Text("Accept terms and conditions") }
            )
        }
    }
}
