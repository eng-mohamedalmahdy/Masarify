package ui.style

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.asFont
import dev.icerock.moko.resources.compose.fontFamilyResource

@Composable
fun AppTypography(): Typography = Typography(
    defaultFontFamily = FontFamily(
        MR.fonts.Tajawal.regular.asFont(weight = FontWeight.Normal)!!,
        MR.fonts.Tajawal.bold.asFont(weight = FontWeight.Bold)!!,
        MR.fonts.Tajawal.extraBold.asFont(weight = FontWeight.ExtraBold)!!,
        MR.fonts.Tajawal.extraLight.asFont(weight = FontWeight.ExtraLight)!!,
        MR.fonts.Tajawal.light.asFont(weight = FontWeight.Light)!!,
        MR.fonts.Tajawal.medium.asFont(weight = FontWeight.Medium)!!,
        MR.fonts.Tajawal.black.asFont(weight = FontWeight.Black)!!,
    ),
    h1 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    h2 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    h3 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h4 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    h5 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    h6 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    subtitle1 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    button = TextStyle(
        color = MaterialTheme.colors.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        color = secondaryTextColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
)
