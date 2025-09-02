package com.lightfeather.designsystem.component

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.theme.AppTheme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Icon as MaterialIcon
enum class IconSize {
    Small,
    Medium,
    Large
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: IconSize = IconSize.Medium
) {
    val iconSize = when (size) {
        IconSize.Small -> AppTheme.dimens.iconSizeSmall
        IconSize.Medium -> AppTheme.dimens.iconSizeMedium
        IconSize.Large -> AppTheme.dimens.iconSizeLarge
    }

    MaterialIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize),
        tint = tint
    )
}

@Composable
fun Icon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: IconSize = IconSize.Medium
) {
    val iconSize = when (size) {
        IconSize.Small -> AppTheme.dimens.iconSizeSmall
        IconSize.Medium -> AppTheme.dimens.iconSizeMedium
        IconSize.Large -> AppTheme.dimens.iconSizeLarge
    }

    MaterialIcon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize),
        tint = tint
    )
}

// No preview for Icon as it requires an actual ImageVector or Painter
