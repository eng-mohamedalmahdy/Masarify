package tech.lightfeather.designsystem.component.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.AppImage
import tech.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Thumbnail image component with optional delete button
 * Displays image in a rounded square container with delete icon overlay
 *
 * @param imageBytes Raw image bytes to display
 * @param onDelete Callback when delete button is clicked (null hides delete button)
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the thumbnail
 */
@Composable
fun ImageThumbnail(
    imageBytes: ByteArray,
    onDelete: (() -> Unit)?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(AppTheme.dimens.component.thumbnail.size)
                .clip(RoundedCornerShape(AppTheme.dimens.component.thumbnail.cornerRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        // Image
        AppImage(
            model = imageBytes,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Delete button overlay
        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppTheme.dimens.extraSmall),
                colors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(MR.strings.delete),
                    modifier = Modifier.size(AppTheme.dimens.icon.size.small),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ImageThumbnailPreview() {
    AppTheme {
        ImageThumbnail(
            imageBytes = ByteArray(0),
            onDelete = {},
            contentDescription = "Preview image",
        )
    }
}
