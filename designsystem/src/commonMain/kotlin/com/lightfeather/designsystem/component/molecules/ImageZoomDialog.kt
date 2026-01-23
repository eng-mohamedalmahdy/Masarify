package com.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource

/**
 * Full-screen image zoom dialog with horizontal pager
 * Supports pinch-to-zoom and pan gestures for each image
 *
 * @param attachments List of attachments to display
 * @param initialPage Index of image to show first
 * @param onDismiss Callback when dialog is dismissed
 * @param modifier Modifier for the dialog
 */
@Composable
fun ImageZoomDialog(
    attachments: List<UiAttachment>,
    initialPage: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(Color.Black),
        ) {
            val pagerState = rememberPagerState(initialPage = initialPage) { attachments.size }

            // Image pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                ZoomableImage(
                    imageBytes = attachments[page].fileContent,
                    contentDescription = attachments[page].name,
                )
            }

            // Top bar with close button and counter
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(AppTheme.dimens.spacing.padding.medium),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(MR.strings.close),
                        tint = Color.White,
                    )
                }

                if (attachments.size > 1) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${attachments.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

/**
 * Zoomable image component with pinch-to-zoom and pan gestures
 */
@Composable
private fun ZoomableImage(
    imageBytes: ByteArray,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state =
        rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)

            // Reset offset when zoomed out completely
            if (scale == 1f) {
                offset = Offset.Zero
            } else {
                val maxX = (scale - 1f) * 1000f
                val maxY = (scale - 1f) * 1000f
                offset =
                    Offset(
                        x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX),
                        y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY),
                    )
            }
        }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AppImage(
            model = imageBytes,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                    ).transformable(state = state),
        )
    }
}
