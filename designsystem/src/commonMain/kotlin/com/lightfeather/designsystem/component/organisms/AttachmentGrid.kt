package com.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.component.atoms.ImageThumbnail
import com.lightfeather.designsystem.component.molecules.ImageZoomDialog
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Grid of attachment thumbnails with zoom capability
 * Displays images in a 3-column grid with tap to zoom
 *
 * @param attachments List of attachments to display
 * @param onDelete Optional callback for deleting attachments (null hides delete buttons)
 * @param modifier Modifier for the grid
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttachmentGrid(
    attachments: List<UiAttachment>,
    onDelete: ((UiAttachment) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    FlowRow(
        modifier = modifier.padding(AppTheme.dimens.small),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
    ) {
        attachments.forEachIndexed { index, attachment ->
            ImageThumbnail(
                imageBytes = attachment.fileContent,
                onDelete =
                    if (onDelete != null) {
                        { onDelete(attachment) }
                    } else {
                        null
                    },
                contentDescription = attachment.name,
            )
        }
    }

    // Show zoom dialog when image is tapped
    selectedImageIndex?.let { index ->
        ImageZoomDialog(
            attachments = attachments,
            initialPage = index,
            onDismiss = { selectedImageIndex = null },
        )
    }
}

@Preview
@Composable
private fun AttachmentGridPreview() {
    AppTheme {
        val dummyAttachments =
            listOf(
                UiAttachment(
                    id = "1",
                    name = "Receipt1.jpg",
                    mimeType = "image/jpeg",
                    fileContent = ByteArray(0),
                ),
                UiAttachment(
                    id = "2",
                    name = "Receipt2.jpg",
                    mimeType = "image/jpeg",
                    fileContent = ByteArray(0),
                ),
                UiAttachment(
                    id = "3",
                    name = "Receipt3.jpg",
                    mimeType = "image/jpeg",
                    fileContent = ByteArray(0),
                ),
            )

        AttachmentGrid(
            attachments = dummyAttachments,
            onDelete = {},
        )
    }
}
