package com.lightfeather.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.lightfeather.designsystem.theme.AppTheme
import androidx.compose.material3.Icon as MaterialIcon

enum class IconSize {
    Small,
    Medium,
    Large,
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: IconSize = IconSize.Medium,
) {
    val iconSize =
        when (size) {
            IconSize.Small -> AppTheme.dimens.iconSizeSmall
            IconSize.Medium -> AppTheme.dimens.iconSizeMedium
            IconSize.Large -> AppTheme.dimens.iconSizeLarge
        }

    MaterialIcon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize),
        tint = tint,
    )
}

@Composable
fun Icon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: IconSize = IconSize.Medium,
) {
    val iconSize =
        when (size) {
            IconSize.Small -> AppTheme.dimens.iconSizeSmall
            IconSize.Medium -> AppTheme.dimens.iconSizeMedium
            IconSize.Large -> AppTheme.dimens.iconSizeLarge
        }

    MaterialIcon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize),
        tint = tint,
    )
}

// No preview for Icon as it requires an actual ImageVector or Painter
