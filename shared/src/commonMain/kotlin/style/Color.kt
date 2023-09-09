package style

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val lightModeColors = Colors(
    primary = Color(0XFF067432),
    onPrimary = Color.White,
    secondary = Color(0XFFFDA329),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0XFFCCCCCC),
    onSurface = Color.Black,
    error = Color(0XFFFF0000),
    primaryVariant = Color.White,    // Error variant for primary color
    secondaryVariant = Color.Black,   // Error variant for secondary color
    onError = Color.White,// Error color,
    isLight = true
)
