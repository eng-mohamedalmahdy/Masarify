package ui.composeables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ui.style.AppTheme


@Composable
fun <T> SearchableGridDropDownMenu(
    options: List<T>,
    onValueChange: (T) -> Unit,
    label: @Composable () -> Unit,
    dropDownItem: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    searchFilterFunction: (String, T) -> Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionIndex by remember { mutableStateOf(-1) }
    var searchText by remember { mutableStateOf("") }
    val filteredList by remember { derivedStateOf { options.filter { searchFilterFunction(searchText, it) } } }

    Column(
        modifier
            .background(AppTheme.cardColor, RoundedCornerShape(9.dp))
            .fillMaxWidth()
            .clickable(onClick = { expanded = !expanded })
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                label()
                if (selectedOptionIndex >= 0) dropDownItem(filteredList[selectedOptionIndex])

            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            Column(modifier = Modifier.width(300.dp).height(320.dp)) {
                SearchTextField(searchText, { searchText = it })
                LazyVerticalGrid(
                    GridCells.Fixed(6), modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    itemsIndexed(filteredList) { idx, value ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    dropDownItem(value)
                                }
                            },
                            onClick = {
                                onValueChange(value)
                                selectedOptionIndex = idx
                                expanded = false
                            },
                            contentPadding = PaddingValues(4.dp)
                        )
                    }
                    item {
                        AddButton(onAddClick)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    IconButton(
        onClick,
        modifier = Modifier
            .padding(2.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(9.dp))
            .clip(RoundedCornerShape(4.dp)),

        ) {
        Image(
            Icons.Default.Add, stringResource(MR.strings.add_new_category_or_tag),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background)
        )
    }
}
