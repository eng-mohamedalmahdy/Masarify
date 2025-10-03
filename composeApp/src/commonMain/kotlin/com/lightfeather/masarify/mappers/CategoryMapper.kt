package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.domain.model.Category

fun Category.toUiCategory(): UiCategory =
    UiCategory(
        id = id.toString(),
        name = name,
        description = description,
        image = icon.takeIf { it.isNotBlank() } ?: "",
        color = color,
    )

fun UiCategory.toCategory(): Category =
    Category(
        id = id.toIntOrNull() ?: -1,
        name = name,
        description = description,
        color = color,
        icon = (image as? String) ?: "",
    )
