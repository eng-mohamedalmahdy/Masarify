package ui.composeables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DropDownTextField(
    items: List<String>,
    label: @Composable () -> Unit,
    onValueChange: (idx: Int, item: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .background(Color.Transparent)
            .clickable(onClick = { isDropdownMenuVisible = true })
    ) {
        CardTextField(
            text = selectedValue,
            onValueChange = {},
            label = label,
            modifier = Modifier.fillMaxWidth(),
            readonlyAndDisabled = true,
            trailingIcon = Icons.Filled.ArrowDropDown
        )
        DropdownMenu(
            expanded = isDropdownMenuVisible,
            onDismissRequest = { isDropdownMenuVisible = false },
            modifier = Modifier.background(Color.White).fillMaxWidth()
        ) {
            items.forEachIndexed { idx, item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedValue = item
                        onValueChange(idx, item)
                        isDropdownMenuVisible = false
                    }
                )
            }
        }
    }

}