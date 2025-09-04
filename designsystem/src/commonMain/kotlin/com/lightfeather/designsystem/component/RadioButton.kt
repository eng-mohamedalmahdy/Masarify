package com.lightfeather.designsystem.component

import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        disabledSelectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUnselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    androidx.compose.material3.RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors
    )
}

@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        disabledSelectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUnselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    Row(
        modifier = modifier
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // We handle changes through the row's clickable modifier
            enabled = enabled,
            colors = colors
        )
        Spacer(modifier = Modifier.width(8.dp))
        label()
    }
}

@Composable
fun RadioGroup(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        disabledSelectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUnselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    Column(modifier = modifier) {
        options.forEach { option ->
            RadioButtonWithLabel(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                label = { Text(option) },
                enabled = enabled,
                colors = colors,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun PreviewRadioButton() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var selected by remember { mutableStateOf(false) }
            
            RadioButton(
                selected = selected,
                onClick = { selected = !selected }
            )
            
            RadioButtonWithLabel(
                selected = selected,
                onClick = { selected = !selected },
                label = { Text("Option 1") }
            )
            
            var selectedOption by remember { mutableStateOf("Option 1") }
            val options = listOf("Option 1", "Option 2", "Option 3")
            
            RadioGroup(
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                options = options
            )
        }
    }
}