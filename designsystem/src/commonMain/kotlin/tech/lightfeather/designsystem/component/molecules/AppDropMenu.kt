package tech.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.stringResource

@Composable
fun <T> AppDropMenu(
    label: String,
    displayedValue: String,
    items: List<T>,
    onSelectedItem: (T) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    containerShape: Shape = MaterialTheme.shapes.small,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    dismissOnHeartClick: Boolean = true,
    dismissOnFooterClick: Boolean = true,
    contentRow:
        @Composable()
        ((T) -> Unit),
    searchFunction: @Composable ((List<T>, String) -> List<T>)? = null,
    headerContent: (@Composable (searchQuery: String) -> Unit)? = null,
    footerContent: (@Composable (searchQuery: String) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems =
        if (searchFunction != null && searchQuery.isNotEmpty()) {
            searchFunction(items, searchQuery)
        } else {
            items
        }

    Box(
        modifier = modifier.clickable { expanded = !expanded },
        contentAlignment = Alignment.CenterStart,
    ) {
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = AppTheme.dimens.small),
            ) {
                AppDropdownContainer(
                    displayedValue,
                    backgroundColor = containerColor,
                    shape = containerShape,
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }

        DropdownMenu(
            shape = MaterialTheme.shapes.large,
            expanded = expanded,
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            properties =
                PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    clippingEnabled = true,
                ),
            modifier =
                Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = AppTheme.dimens.medium)
                    .heightIn(max = AppTheme.dimens.massive * 8)
                    .padding(vertical = AppTheme.dimens.small)
                    .align(Alignment.Center),
        ) {
            // Header content
            Box(
                modifier =
                    Modifier.clickable(
                        enabled = dismissOnHeartClick,
                        onClick = {
                            expanded = false
                            searchQuery = ""
                        },
                    ),
            ) {
                headerContent?.invoke(searchQuery)
            }

            // Search field if search function is provided
            if (searchFunction != null) {
                DropdownMenuItem(
                    onClick = { /* Do nothing - just focus search */ },
                    text = {
                        SearchTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            onSearch = { /* Search happens automatically */ },
                            placeholder = stringResource(MR.strings.filter_text_search),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
            }

            // Items
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        searchQuery = ""
                        onSelectedItem(item)
                    },
                    modifier = Modifier.padding(vertical = AppTheme.dimens.medium),
                    text = { contentRow(item) },
                )
            }

            // Footer content
            Box(
                modifier =
                    Modifier.clickable(
                        enabled = dismissOnFooterClick,
                        onClick = {
                            expanded = false
                            searchQuery = ""
                        },
                    ),
            ) {
                footerContent?.invoke(searchQuery)
            }
        }
    }
}

@Composable
fun AppDropdownContainer(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = MaterialTheme.shapes.small,
    labelStyle: TextStyle = AppTheme.typography.labelLarge,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                // Match text field's padding
                .border(
                    width = AppTheme.dimens.hairline,
                    color = MaterialTheme.colorScheme.outline,
                    shape = shape,
                ).clip(shape)
                .background(backgroundColor) // Match container background
                .padding(AppTheme.dimens.default), // Inner padding
    ) {
        Text(
            text = label,
            modifier =
                Modifier
                    .weight(4f)
                    .padding(vertical = AppTheme.dimens.small),
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        content()
    }
}

@Preview
@Composable
fun AppDropMenuPreview() {
    AppTheme {
        AppDropMenu(
            containerColor = MaterialTheme.colorScheme.surface,
            label = "Select an item",
            displayedValue = "Item 1",
            items = listOf("Item 1", "Item 2", "Item 3"),
            onSelectedItem = { selectedItem ->
                // Handle item selection
            },
            modifier = Modifier.fillMaxWidth(),
            contentRow = { item ->
                Text(text = item)
            },
        )
    }
}
