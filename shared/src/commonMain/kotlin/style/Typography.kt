package style

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.F
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
)