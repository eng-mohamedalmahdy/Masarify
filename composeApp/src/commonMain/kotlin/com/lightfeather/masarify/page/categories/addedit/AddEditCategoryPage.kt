package com.lightfeather.masarify.page.categories.addedit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.SecondaryButton
import com.lightfeather.designsystem.component.molecules.dialog.ColorPickerDialog
import com.lightfeather.designsystem.component.organisms.listitem.CategoryListItem
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.theme.rememberAppWindowSizeClass
import com.lightfeather.designsystem.util.colorToHex
import com.lightfeather.designsystem.util.parseColor
import com.lightfeather.designsystem.util.toColorInt
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.lightfeather.designsystem.MR as DSMR

@Composable
fun AddEditCategoryPage(
    viewModel: AddEditCategoryPageViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    AddEditCategoryPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
internal fun AddEditCategoryPageContent(
    state: AddEditCategoryPageState,
    onIntent: (AddEditCategoryPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = rememberAppWindowSizeClass()
    val isCompact = windowSizeClass == WindowWidthSizeClass.Compact
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(AppTheme.dimens.spacing.padding.medium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
    ) {
        // Header
        Text(
            text =
                if (state.isEditMode) {
                    stringResource(MR.strings.edit_category)
                } else {
                    stringResource(MR.strings.add_category)
                },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))
        // Name Field
        CategoryPreview(state = state)

        TextField(
            value = state.name,
            onValueChange = { onIntent(AddEditCategoryPageIntent.UpdateName(it)) },
            label = stringResource(MR.strings.category_name),
            placeholder = stringResource(MR.strings.enter_category_name),
            modifier = Modifier.fillMaxWidth(),
        )

        // Description Field
        TextField(
            value = state.description,
            onValueChange = { onIntent(AddEditCategoryPageIntent.UpdateDescription(it)) },
            label = stringResource(MR.strings.description_optional),
            placeholder = stringResource(MR.strings.enter_category_description),
            modifier = Modifier.fillMaxWidth(),
        )

        // Custom Icon URL Field
        TextField(
            value = state.customIconUrl,
            onValueChange = { onIntent(AddEditCategoryPageIntent.UpdateCustomIconUrl(it)) },
            label = stringResource(MR.strings.custom_icon_url),
            placeholder = stringResource(MR.strings.enter_custom_icon_url),
            modifier = Modifier.fillMaxWidth(),
        )
        if (isCompact) {
            Column {
                ColorPickerWithIcons(state, Modifier, onIntent)
            }
        } else {
            Row {
                ColorPickerWithIcons(state, Modifier.weight(1f), onIntent)
            }
        }
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SecondaryButton(
                onClick = { onIntent(AddEditCategoryPageIntent.Cancel) },
                enabled = !state.isLoading,
            ) {
                Text(stringResource(DSMR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(AppTheme.dimens.spacing.padding.medium))

            PrimaryButton(
                onClick = { onIntent(AddEditCategoryPageIntent.Save) },
                enabled = state.isValid && !state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(MR.strings.save))
                }
            }
        }
    }

    // Color Picker Dialog
    if (state.showColorPicker) {
        ColorPickerDialog(
            initialColor = parseColor(state.selectedColor),
            savedColors = state.recentColors,
            onColorChange = { color ->
                val hex = colorToHex(color)
                onIntent(AddEditCategoryPageIntent.SelectColor(hex))
            },
            onDismiss = { onIntent(AddEditCategoryPageIntent.ToggleColorPicker(false)) },
            onConfirm = { color ->
                val hex = colorToHex(color)
                onIntent(AddEditCategoryPageIntent.SelectColor(hex))
                onIntent(AddEditCategoryPageIntent.ToggleColorPicker(false))
                onIntent(AddEditCategoryPageIntent.SaveRecentColor(hex))
            },
        )
    }
}

@Composable
private fun ColorPickerWithIcons(
    state: AddEditCategoryPageState,
    containersModifier: Modifier,
    onIntent: (AddEditCategoryPageIntent) -> Unit,
) {
    val windowSizeClass = rememberAppWindowSizeClass()
    val isCompact = windowSizeClass == WindowWidthSizeClass.Compact

    // Color Picker
    Column(
        modifier = containersModifier,
    ) {
        Text(
            text = stringResource(MR.strings.color),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

        Card(
            onClick = { onIntent(AddEditCategoryPageIntent.ToggleColorPicker(true)) },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTheme.shapes.medium,
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Row(
                modifier = Modifier.padding(AppTheme.dimens.spacing.padding.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.huge)
                                .padding(AppTheme.dimens.spacing.padding.tiny),
                    ) {
                        Card(
                            shape = AppTheme.shapes.small,
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        runCatching { Color(state.selectedColor.toColorInt()) }
                                            .getOrElse { MaterialTheme.colorScheme.primary },
                                ),
                            modifier = Modifier.fillMaxSize(),
                        ) {}
                    }
                    Text(
                        text = state.selectedColor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = stringResource(MR.strings.select_color),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Recent Colors Section (Non-Compact Mode Only)
        if (!isCompact) {
            Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
            RecentColorsSection(state = state, onIntent = onIntent)
        }
    }

    Spacer(Modifier.size(AppTheme.dimens.spacing.margin.small))
    Column(containersModifier) {
        Text(
            text = stringResource(MR.strings.icon),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

        if (state.isLoadingIcons) {
            Box(
                modifier = Modifier.fillMaxWidth().height(AppTheme.dimens.massive * 3),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (state.availableIcons.isEmpty()) {
            EmptyState(
                title = stringResource(MR.strings.no_icons_available),
                message = stringResource(MR.strings.no_icons_message),
                icon = Icons.Outlined.Category,
                modifier = Modifier.fillMaxWidth().height(AppTheme.dimens.massive * 3),
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.shapes.medium,
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(AppTheme.dimens.huge),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(AppTheme.dimens.massive * 3)
                            .padding(AppTheme.dimens.spacing.padding.small),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                ) {
                    items(
                        items = state.availableIcons,
                        key = { it },
                    ) { iconUrl ->
                        IconGridItem(
                            iconUrl = iconUrl,
                            isSelected = state.selectedIcon == iconUrl,
                            onClick = { onIntent(AddEditCategoryPageIntent.SelectIcon(iconUrl)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentColorsSection(
    state: AddEditCategoryPageState,
    onIntent: (AddEditCategoryPageIntent) -> Unit,
) {
    Column {
        Text(
            text = stringResource(MR.strings.recent_colors),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

        if (state.recentColors.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.shapes.medium,
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.dimens.spacing.padding.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(MR.strings.no_recent_colors),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(MR.strings.tap_color_to_select),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.shapes.medium,
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(AppTheme.dimens.spacing.padding.medium),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                ) {
                    state.recentColors.forEach { color ->
                        Card(
                            onClick = {
                                onIntent(AddEditCategoryPageIntent.SelectColor(color))
                            },
                            shape = AppTheme.shapes.small,
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        runCatching { Color(color.toColorInt()) }
                                            .getOrElse { MaterialTheme.colorScheme.primary },
                                ),
                            modifier = Modifier.size(AppTheme.dimens.huge),
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun IconGridItem(
    iconUrl: Any,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(AppTheme.dimens.huge),
    ) {
        Card(
            onClick = onClick,
            shape = AppTheme.shapes.small,
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                ),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                AppImage(
                    model = iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppTheme.dimens.spacing.padding.tiny)
                        .size(AppTheme.dimens.icon.size.small),
            )
        }
    }
}

@Composable
private fun CategoryPreview(state: AddEditCategoryPageState) {
    val previewIcon = state.customIconUrl.ifBlank { state.selectedIcon ?: Any() }

    val previewCategory =
        UiCategory(
            id = "preview",
            name = state.name.takeIf { it.isNotBlank() } ?: stringResource(MR.strings.category_name),
            description = state.description.takeIf { it.isNotBlank() },
            image = previewIcon,
            color = state.selectedColor,
        )

    CategoryListItem(
        category = previewCategory,
        isSelected = false,
        onClick = {},
        onDelete = null,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
private fun AddEditCategoryPagePreview() {
    AppTheme {
        AddEditCategoryPageContent(
            state =
                AddEditCategoryPageState(
                    name = "Food",
                    description = "Food and groceries",
                    selectedColor = "#4CAF50",
                    selectedIcon = "https://img.icons8.com/ios/50/food.png",
                    availableIcons =
                        listOf(
                            "https://img.icons8.com/ios/50/food.png",
                            "https://img.icons8.com/ios/50/car.png",
                            "https://img.icons8.com/ios/50/home.png",
                        ),
                ),
            onIntent = {},
        )
    }
}
