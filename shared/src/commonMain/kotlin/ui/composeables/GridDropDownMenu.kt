package ui.composeables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun <T> GridDropDownMenu(
    options: List<T>,
    onValueChange: (T) -> Unit,
    label: @Composable () -> Unit,
    dropDownItem: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }

    Column(
        modifier
            .background(Color.Transparent)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = !expanded })
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                label()
                if (selectedOptionIndex >= 0) dropDownItem(options[selectedOptionIndex])

            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(Modifier.fillMaxWidth())

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            Box(modifier = Modifier.width(300.dp).height(300.dp)) {
                LazyVerticalGrid(
                    GridCells.Fixed(6), modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    itemsIndexed(options) { idx, value ->
                        DropdownMenuItem(
                            onClick = {
                                onValueChange(value)
                                selectedOptionIndex = idx
                                expanded = false
                            },
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Row(
                                Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                dropDownItem(value)
                            }
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))
    }
}
