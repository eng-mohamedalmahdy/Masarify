package tech.lightfeather.designsystem.component.molecules

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.io.encoding.Base64

@Suppress("CyclomaticComplexMethod") // Composable: handles multiple image loading scenarios
@Composable
fun AppImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    errorPlaceholder: DrawableResource? = null,
    placeholder: DrawableResource? = null,
    mokoPlaceholder: ImageResource? = null,
) {
    val isPreview = LocalInspectionMode.current
    val isUrl =
        when (model) {
            is String -> model.startsWith("http://") || model.startsWith("https://")
            else -> false
        }
    if (isUrl.not() && model is String) {
        val decoded = Base64.decode(model)
        AsyncImage(
            model = decoded,
            error = errorPlaceholder?.let { painterResource(resource = it) },
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = placeholder?.let { painterResource(resource = it) },
            onLoading = { onState?.invoke(it) },
            onSuccess = { onState?.invoke(it) },
            onError = { onState?.invoke(it) },
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
        )
        return
    }

    if (isPreview) {
        Image(
            painter =
                placeholder?.let { painterResource(resource = it) }
                    ?: mokoPlaceholder?.let { painterResource(mokoPlaceholder) }
                    ?: ColorPainter(Color.Gray),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    } else if (model is Painter) {
        Image(
            painter = model,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    } else if (model is DrawableResource) {
        Image(
            painter = painterResource(model),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    } else if (model is ImageVector) {
        Image(
            imageVector = model,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    } else {
        AsyncImage(
            model = model,
            error = errorPlaceholder?.let { painterResource(resource = it) },
            contentDescription = contentDescription,
            modifier = modifier,
            placeholder = placeholder?.let { painterResource(resource = it) },
            onLoading = { onState?.invoke(it) },
            onSuccess = { onState?.invoke(it) },
            onError = { onState?.invoke(it) },
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
        )
    }
}

@Preview
@Composable
private fun PreviewAppImage() {
    Column {
        AppImage(
            "",
            contentDescription = null,
        )
    }
}
