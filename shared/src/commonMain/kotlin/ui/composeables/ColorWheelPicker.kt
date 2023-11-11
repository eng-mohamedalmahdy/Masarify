import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

@Composable
fun ColorWheel(
    selectedColor: MutableState<Color>,
    modifier: Modifier = Modifier,
) {
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    val density = LocalDensity.current.density

    Box(modifier = modifier) {
        Canvas(
            modifier = modifier
                .onGloballyPositioned {
                    val center = Offset(
                        (it.size.width / 2).toFloat(),
                        (it.size.height / 2).toFloat()
                    )
                    offset = center
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, panGesture, _, _ ->
                        val wheelRadius: Float =
                            max((50.dp * density).value, min(size.width.toFloat(), size.height.toFloat()) / 2)
                        val strokeWidth = 10f

                        val outerRadius = wheelRadius - strokeWidth / 2

                        val center = Offset(
                            (size.width / 2).toFloat(),
                            (size.height / 2).toFloat()
                        )
                        // Calculate the new offset based on the pan gesture
                        val newOffset = offset + panGesture

                        // Calculate the distance from the center
                        val distance = hypot(newOffset.x - center.x, newOffset.y - center.y)

                        // If the distance is within the circle radius, update the offset
                        if (distance <= outerRadius) {
                            offset = newOffset
                        }
                    }
                }
        ) {
            val wheelRadius = max((50.dp * density).value, min(size.width, size.height) / 2)

            val strokeWidth = 10f
            val colorCount = 360
            val colorAngle = 360f / colorCount

            val outerRadius = wheelRadius - strokeWidth / 2

            repeat(colorCount) { i ->
                val startAngle = i * colorAngle
                val endAngle = startAngle + colorAngle
                val color = hsvToRgb(startAngle, 1f, 1f)

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = colorAngle,
                    useCenter = true,
                    size = Size(outerRadius * 2, outerRadius * 2),
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    style = Stroke(strokeWidth)
                )

            }

            // Convert absolute touch coordinates to canvas-relative coordinates
            val touchX = (offset.x - center.x).coerceIn(-outerRadius, outerRadius) + outerRadius
            val touchY = (offset.y - center.y).coerceIn(-outerRadius, outerRadius) + outerRadius

            val selectedHue = getHue(touchX, touchY, outerRadius, outerRadius)
            selectedColor.value = hsvToRgb(selectedHue, 1f, 1f)

            // Draw the selected color circle
            // Draw the selected color circle with inverse color stroke
            drawCircle(
                color = selectedColor.value,
                radius = wheelRadius / 20f,
                center = Offset(touchX, touchY)
            )

            drawCircle(
                color = Color.White, // Inverse color for stroke
                radius = (wheelRadius / 20f), // Adjust radius for stroke
                center = Offset(touchX, touchY),
                style = Stroke(wheelRadius / 20)
            )
        }
    }
}


private fun hsvToRgb(h: Float, s: Float, v: Float): Color {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60) % 2 - 1))
    val m = v - c

    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(r, g, b)
}

private fun getHue(x: Float, y: Float, centerX: Float, centerY: Float): Float {
    val angle = atan2(y - centerY, x - centerX)
    var hue = angle * (180f / PI)
    if (hue < 0) hue += 360f
    return hue.toFloat()
}
