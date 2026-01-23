package com.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.getLocalizedDescription
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.designsystem.util.toColorInt
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryListItem(
    category: UiCategory,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val categoryColor = runCatching { Color(category.color.toColorInt()) }.getOrElse { surfaceColor }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level1,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.spacing.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Category Icon with colored background
            Card(
                shape = AppTheme.shapes.small,
                colors =
                    CardDefaults.cardColors(
                        containerColor = categoryColor,
                    ),
                modifier = Modifier.size(AppTheme.dimens.icon.size.large),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppImage(
                        model = category.image,
                        contentDescription = category.name,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    )
                }
            }

            // Category Details
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = category.getLocalizedName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!category.description.isNullOrBlank()) {
                    Text(
                        text = category.getLocalizedDescription().orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Delete Button (optional)
            onDelete?.let { deleteCallback ->
                IconButton(onClick = deleteCallback) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(MR.strings.delete_category),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryListItemPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(AppTheme.dimens.default)) {
            CategoryListItem(
                category = UiCategory.dummy,
                isSelected = false,
                onClick = {},
                onDelete = {},
            )
        }
    }
}

@Preview
@Composable
private fun CategoryListItemSelectedPreview() {
    AppTheme {
        Column(modifier = Modifier.padding(AppTheme.dimens.default)) {
            CategoryListItem(
                category = UiCategory.dummy,
                isSelected = true,
                onClick = {},
                onDelete = {},
            )
        }
    }
}

@Preview
@Composable
private fun CategoryListItemNoDeletePreview() {
    AppTheme {
        Column(modifier = Modifier.padding(AppTheme.dimens.default)) {
            CategoryListItem(
                category =
                    UiCategory(
                        id = "2",
                        name = "Transportation",
                        description = null,
                        image = "",
                        color = "#2196F3",
                    ),
                isSelected = false,
                onClick = {},
                onDelete = null,
            )
        }
    }
}
