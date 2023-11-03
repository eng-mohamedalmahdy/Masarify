package ui.style

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.asFont


object AppTheme {
    val lightModeColors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.copy(
            primary = Color(0XFF067432),
            onPrimary = Color.White,
            secondary = Color(0XFFFDA329),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color(0xFF292B2D),
            surface = Color(0XFFCCCCCC),
            onSurface = Color.Black,
            error = Color(0XFFFF0000),
        )


    val AppTypography : Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.copy()
//    h1 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 32.sp
//    ),
//    h2 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 28.sp
//    ),
//    h3 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 24.sp
//    ),
//    h4 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 20.sp
//    ),
//    h5 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 18.sp
//    ),
//    h6 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 16.sp
//    ),
//    subtitle1 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
//    subtitle2 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Medium,
//        fontSize = 14.sp
//    ),
//    body1 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
//    body2 = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Normal,
//        fontSize = 14.sp
//    ),
//    button = TextStyle(
//        color = MaterialTheme.colors.onPrimary,
//        fontWeight = FontWeight.Bold,
//        fontSize = 14.sp
//    ),
//    caption = TextStyle(
//        color = secondaryTextColor,
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 12.sp
//    ),
//    overline = TextStyle(
//        color = MaterialTheme.colors.onBackground,
//        fontWeight = FontWeight.Bold,
//        fontSize = 10.sp
//    )

    val expenseColor = Color(0XFFFD3C4A)
    val incomeColor = Color(0XFF067432)
    val transferColor = Color(0xFFFDD835)
    val cardColor = Color(0xFFFAFAFA)
    val secondaryTextColor = Color(0xFF91919F)
    val textFieldIndicatorColor = Color(0xFF99AEBC)
    val grayColor = Color(0xFFAEAEAE)
}