package com.lightfeather.designsystem.component.molecules.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.SecondaryButton
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.colorToHex
import com.lightfeather.designsystem.util.parseColor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

// KMP-compatible utility functions
private fun colorToHSV(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val hue =
        when {
            delta == 0f -> 0f
            max == r -> ((g - b) / delta) * 60f
            max == g -> ((b - r) / delta + 2f) * 60f
            else -> ((r - g) / delta + 4f) * 60f
        }.let { if (it < 0) it + 360f else it }

    val saturation = if (max == 0f) 0f else delta / max
    val value = max

    return floatArrayOf(hue, saturation, value)
}

@Composable
fun ColorPickerDialog(
    initialColor: Color = Color.Magenta,
    savedColors: List<String> = emptyList(),
    onColorChange: (Color) -> Unit = {},
    onRgbaChange: (Int, Int, Int, Float) -> Unit = { _, _, _, _ -> },
    onSavedColorClick: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    onConfirm: (Color) -> Unit = {},
) {
    var currentColor by remember { mutableStateOf(initialColor) }
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha) }

    // Convert initial color to HSV
    remember(initialColor) {
        val hsv = colorToHSV(initialColor)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        alpha = initialColor.alpha
        currentColor = initialColor
    }

    // Update current color when HSV changes (preserve alpha)
    val updateColor = {
        val newColor = Color.hsv(hue, saturation, value, alpha)
        currentColor = newColor
        onColorChange(newColor)
        onRgbaChange(
            (currentColor.red * 255).roundToInt(),
            (currentColor.green * 255).roundToInt(),
            (currentColor.blue * 255).roundToInt(),
            currentColor.alpha,
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.default),
            shape = RoundedCornerShape(AppTheme.dimens.large),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(AppTheme.dimens.default),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
            ) {
                // HSV Color Picker (full width)
                HSVColorPicker(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onHueChange = {
                        hue = it
                        updateColor()
                    },
                    onSaturationValueChange = { s, v ->
                        saturation = s
                        value = v
                        updateColor()
                    },
                )

                // Alpha slider
                AlphaSlider(
                    alpha = alpha,
                    color = Color.hsv(hue, saturation, value),
                    onAlphaChange = {
                        alpha = it
                        updateColor()
                    },
                )

                // Color preview and hex field
                ColorPreviewWithHex(
                    color = currentColor,
                    onHexChange = { hexString ->
                        handleHexColorChange(
                            hexString,
                            alpha,
                            { color, hsv ->
                                currentColor = color
                                hue = hsv[0]
                                saturation = hsv[1]
                                value = hsv[2]
                            },
                            onColorChange,
                            onRgbaChange,
                        )
                    },
                )

                // Saved colors
                if (savedColors.isNotEmpty()) {
                    SavedColorsGrid(
                        savedColors = savedColors,
                        onColorClick = { colorString ->
                            handleSavedColorClick(
                                colorString,
                                alpha,
                                { color, hsv ->
                                    currentColor = color
                                    hue = hsv[0]
                                    saturation = hsv[1]
                                    value = hsv[2]
                                },
                                onSavedColorClick,
                                onColorChange,
                                onRgbaChange,
                            )
                        },
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SecondaryButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(AppTheme.dimens.medium))
                    SecondaryButton(onClick = { onConfirm(currentColor) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPreviewWithHex(
    color: Color,
    onHexChange: (String) -> Unit,
) {
    var hexValue by remember { mutableStateOf(colorToHex(color)) }
    var isUserTyping by remember { mutableStateOf(false) }

    // Update hex field when color changes from external sources (sliders, saved colors)
    LaunchedEffect(color) {
        if (!isUserTyping) {
            hexValue = colorToHex(color)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
    ) {
        // Circular color preview with transparency support
        Box(
            modifier =
                Modifier
                    .size(AppTheme.dimens.huge)
                    .clip(RoundedCornerShape(AppTheme.dimens.medium)),
        ) {
            // Transparent checkerboard background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val checkerSize = 8f
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)

                for (x in 0 until (size.width / checkerSize).toInt()) {
                    for (y in 0 until (size.height / checkerSize).toInt()) {
                        val xPos = x * checkerSize
                        val yPos = y * checkerSize
                        val checkerCenter = Offset(xPos + checkerSize / 2, yPos + checkerSize / 2)
                        val distance =
                            sqrt(
                                (checkerCenter.x - center.x) * (checkerCenter.x - center.x) +
                                    (checkerCenter.y - center.y) * (checkerCenter.y - center.y),
                            )

                        // Only draw checkers inside the circle
                        if (distance <= radius) {
                            val isEven = (x + y) % 2 == 0
                            val checkerColor = if (isEven) Color.White else Color.LightGray
                            drawRect(
                                color = checkerColor,
                                topLeft = Offset(xPos, yPos),
                                size =
                                    Size(checkerSize, checkerSize),
                            )
                        }
                    }
                }
            }

            // Color overlay
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color),
            )
        }

        // Hex value text field
        TextField(
            value = hexValue,
            onValueChange = { newHex ->
                isUserTyping = true
                hexValue = newHex

                // Only validate and update when length is exactly 7 or 9 characters (including #)
                if ((newHex.length == 7 || newHex.length == 9) && newHex.startsWith("#")) {
                    try {
                        // Try to parse as hex color
                        onHexChange(newHex)
                        isUserTyping = false
                    } catch (_: Exception) {
                        // Invalid hex format, keep typing state
                    }
                }
                // For any other length, just let user type freely
            },
            label = "Hex Color".takeIf { hexValue.isEmpty() },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                ),
        )
    }
}

@Composable
private fun HSVColorPicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onHueChange: (Float) -> Unit,
    onSaturationValueChange: (Float, Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default)) {
//        // Saturation-Value picker
        SaturationValuePicker(
            hue = hue,
            saturation = saturation,
            value = value,
            onSaturationValueChange = onSaturationValueChange,
        )

        // Hue slider
        HueSlider(
            hue = hue,
            onHueChange = onHueChange,
        )
    }
}

@Composable
private fun SaturationValuePicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChange: (Float, Float) -> Unit,
) {
    val pickerHeight = AppTheme.dimens.massive * 3 + AppTheme.dimens.medium
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(pickerHeight)
                .clip(RoundedCornerShape(AppTheme.dimens.medium))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (!isDragging) {
                            val newSaturation = (offset.x / size.width).coerceIn(0f, 1f)
                            val newValue = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                            onSaturationValueChange(newSaturation, newValue)
                        }
                    }
                }.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            // Update position on drag start
                            val newSaturation = (offset.x / size.width).coerceIn(0f, 1f)
                            val newValue = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                            onSaturationValueChange(newSaturation, newValue)
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                    ) { change, _ ->
                        // Use the actual pointer position, not drag amount
                        val currentX = change.position.x
                        val currentY = change.position.y
                        val newSaturation = (currentX / size.width).coerceIn(0f, 1f)
                        val newValue = 1f - (currentY / size.height).coerceIn(0f, 1f)
                        onSaturationValueChange(newSaturation, newValue)
                    }
                },
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(pickerHeight),
        ) {
            val width = size.width
            val height = size.height

            // Draw base hue color
            val baseColor = Color.hsv(hue, 1f, 1f)
            drawRect(color = baseColor, size = size)

            // Draw saturation gradient (left to right: white to transparent)
            val saturationBrush =
                Brush.horizontalGradient(
                    colors = listOf(Color.White, Color.Transparent),
                )
            drawRect(brush = saturationBrush, size = size)

            // Draw value gradient (top to bottom: transparent to black)
            val valueBrush =
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                )
            drawRect(brush = valueBrush, size = size)

            // Draw current position indicator
            val indicatorX = saturation * width
            val indicatorY = (1f - value) * height
            drawCircle(
                color = Color.White,
                radius = 8f,
                center = Offset(indicatorX, indicatorY),
            )
            drawCircle(
                color = Color.Black,
                radius = 6f,
                center = Offset(indicatorX, indicatorY),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(AppTheme.dimens.large), // Full height for thumb space
    ) {
        // Gradient background - centered within the larger container
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.default)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(AppTheme.dimens.compact)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val hueColors =
                    listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red,
                    )
                val brush = Brush.horizontalGradient(hueColors)
                drawRect(brush, size = size)
            }
        }

        // Slider spans full container height
        Slider(
            value = hue,
            onValueChange = onHueChange,
            valueRange = 0f..360f,
            colors =
                SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                ),
            modifier = Modifier.fillMaxSize(),
            thumb = {
                HueSliderThumb(hue = hue)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlphaSlider(
    alpha: Float,
    color: Color,
    onAlphaChange: (Float) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(AppTheme.dimens.large), // Full height for thumb space
    ) {
        // Alpha gradient background - centered within the larger container
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimens.default)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(AppTheme.dimens.compact)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw checkerboard pattern for transparency
                val checkerSize = 8f
                for (x in 0 until (size.width / checkerSize).toInt()) {
                    for (y in 0 until (size.height / checkerSize).toInt()) {
                        val isEven = (x + y) % 2 == 0
                        val checkerColor = if (isEven) Color.White else Color.LightGray
                        drawRect(
                            color = checkerColor,
                            topLeft = Offset(x * checkerSize, y * checkerSize),
                            size =
                                Size(checkerSize, checkerSize),
                        )
                    }
                }

                // Draw alpha gradient
                val brush =
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0f), color.copy(alpha = 1f)),
                    )
                drawRect(brush, size = size)
            }
        }

        // Slider spans full container height
        Slider(
            value = alpha,
            onValueChange = onAlphaChange,
            valueRange = 0f..1f,
            colors =
                SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                ),
            modifier = Modifier.fillMaxSize(),
            thumb = {
                AlphaSliderThumb(alpha = alpha, color = color)
            },
        )
    }
}

@Composable
private fun HueSliderThumb(hue: Float) {
    val currentHue = hue.coerceIn(0f, 360f)
    val prevHue = (currentHue - 30f).let { if (it < 0f) it + 360f else it }
    val nextHue = (currentHue + 30f).let { if (it > 360f) it - 360f else it }

    val currentColor = Color.hsv(currentHue, 1f, 1f)
    val prevColor = Color.hsv(prevHue, 1f, 1f)
    val nextColor = Color.hsv(nextHue, 1f, 1f)

    Canvas(
        modifier = Modifier.size(AppTheme.dimens.large),
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Outer gradient ring with neighboring colors
        val gradientBrush =
            Brush.sweepGradient(
                colors = listOf(prevColor, currentColor, nextColor, currentColor, prevColor),
                center = center,
            )

        // Draw outer gradient ring
        drawCircle(
            brush = gradientBrush,
            radius = radius,
            center = center,
        )

        // Draw white separator ring
        drawCircle(
            color = Color.White,
            radius = radius * 0.75f,
            center = center,
            style = Stroke(width = radius * 0.1f),
        )

        // Draw inner current color circle
        drawCircle(
            color = currentColor,
            radius = radius * 0.65f,
            center = center,
        )

        // Draw black center circle
        drawCircle(
            color = Color.Black,
            radius = radius * 0.3f,
            center = center,
        )

        // Add glossy highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = radius * 0.2f,
            center = center + Offset(-radius * 0.2f, -radius * 0.2f),
        )
    }
}

@Composable
private fun AlphaSliderThumb(
    alpha: Float,
    color: Color,
) {
    Canvas(
        modifier = Modifier.size(AppTheme.dimens.extraLarge),
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Draw checkerboard background for transparency visualization
        val checkerSize = radius * 0.2f
        for (i in -3..3) {
            for (j in -3..3) {
                val x = center.x + i * checkerSize
                val y = center.y + j * checkerSize
                val distance = sqrt((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y))
                if (distance <= radius) {
                    val isEven = (i + j) % 2 == 0
                    val checkerColor = if (isEven) Color.White else Color.LightGray
                    drawRect(
                        color = checkerColor,
                        topLeft = Offset(x - checkerSize / 2, y - checkerSize / 2),
                        size =
                            Size(checkerSize, checkerSize),
                    )
                }
            }
        }

        // Draw alpha gradient overlay
        val alphaBrush =
            Brush.radialGradient(
                colors =
                    listOf(
                        color.copy(alpha = 0.1f),
                        color.copy(alpha = alpha * 0.7f),
                        color.copy(alpha = alpha),
                    ),
                center = center,
                radius = radius,
            )

        drawCircle(
            brush = alphaBrush,
            radius = radius,
            center = center,
        )

        // Draw white border ring
        drawCircle(
            color = Color.White,
            radius = radius * 0.8f,
            center = center,
            style = Stroke(width = radius * 0.08f),
        )

        // Draw inner alpha indicator
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = radius * 0.7f,
            center = center,
        )

        // Draw black center with current alpha
        drawCircle(
            color = Color.Black.copy(alpha = min(alpha + 0.3f, 1f)),
            radius = radius * 0.25f,
            center = center,
        )

        // Add shine effect
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = radius * 0.15f,
            center = center + Offset(-radius * 0.25f, -radius * 0.25f),
        )
    }
}

@Composable
private fun SavedColorsGrid(
    savedColors: List<String>,
    onColorClick: (String) -> Unit,
) {
    Column {
        Text(
            text = "Saved Colors",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = AppTheme.dimens.default),
        )

        // Sin wave layout for color circles
        SinWaveColorLayout(
            colors = savedColors,
            onColorClick = onColorClick,
        )
    }
}

@Composable
private fun SinWaveColorLayout(
    colors: List<String>,
    onColorClick: (String) -> Unit,
) {
    // Create simple grid layout
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.extraSmall),
    ) {
        if (colors.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = AppTheme.dimens.small),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
            ) {
                colors.forEach { colorString ->
                    val color = parseColor(colorString)
                    Box(
                        modifier =
                            Modifier
                                .size(AppTheme.dimens.huge)
                                .clip(RoundedCornerShape(AppTheme.dimens.medium))
                                .background(color)
                                .clickable { onColorClick(colorString) },
                    )
                }
            }
        }
    }
}

// Helper functions to reduce ColorPickerDialog length
private fun handleHexColorChange(
    hexString: String,
    alpha: Float,
    updateState: (Color, FloatArray) -> Unit,
    onColorChange: (Color) -> Unit,
    onRgbaChange: (Int, Int, Int, Float) -> Unit,
) {
    try {
        val newColor = parseColor(hexString)
        val colorWithAlpha = newColor.copy(alpha = alpha)
        val hsv = colorToHSV(colorWithAlpha)
        updateState(colorWithAlpha, hsv)
        onColorChange(colorWithAlpha)
        onRgbaChange(
            (colorWithAlpha.red * 255).roundToInt(),
            (colorWithAlpha.green * 255).roundToInt(),
            (colorWithAlpha.blue * 255).roundToInt(),
            colorWithAlpha.alpha,
        )
    } catch (_: Exception) {
        // Invalid hex, ignore
    }
}

private fun handleSavedColorClick(
    colorString: String,
    alpha: Float,
    updateState: (Color, FloatArray) -> Unit,
    onSavedColorClick: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onRgbaChange: (Int, Int, Int, Float) -> Unit,
) {
    val color = parseColor(colorString)
    val colorWithAlpha = color.copy(alpha = alpha)
    val hsv = colorToHSV(colorWithAlpha)
    updateState(colorWithAlpha, hsv)
    onSavedColorClick(colorString)
    onColorChange(colorWithAlpha)
    onRgbaChange(
        (colorWithAlpha.red * 255).roundToInt(),
        (colorWithAlpha.green * 255).roundToInt(),
        (colorWithAlpha.blue * 255).roundToInt(),
        colorWithAlpha.alpha,
    )
}
