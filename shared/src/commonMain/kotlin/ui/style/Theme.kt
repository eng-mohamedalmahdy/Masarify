package ui.style

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.fontFamilyResource

interface AppTheme {
    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get

    val typography: Typography
        @Composable @ReadOnlyComposable get

    val expenseColor: Color
    val deleteColor: Color
    val updateColor: Color
    val incomeColor: Color
    val transferColor: Color
    val cardColor: Color
    val secondaryTextColor: Color
    val textFieldIndicatorColor: Color
    val grayColor: Color
}

object AppLightTheme : AppTheme {
    override val colorScheme: ColorScheme
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

    override val expenseColor = Color(0XFFFD3C4A)
    override val deleteColor = Color(0XFFFD3C4A)
    override val updateColor = Color(0xFF2196F3)
    override val incomeColor = Color(0XFF067432)
    override val transferColor = Color(0xFFFDD835)
    override val cardColor = Color(0xFFFAFAFA)
    override val secondaryTextColor = Color(0xFF91919F)
    override val textFieldIndicatorColor = Color(0xFF99AEBC)
    override val grayColor = Color(0xFFAEAEAE)

    override val typography: Typography
        @Composable
        get() = MaterialTheme.typography.copy(
            displayLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            displayMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            displaySmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            headlineLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            headlineMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            headlineSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            titleLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            titleMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)

            ),
            titleSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.medium)
            ),
            bodyLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)
            ),
            bodyMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)

            ),
            labelLarge = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            bodySmall = TextStyle(
                color = secondaryTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.medium)

            ),
            labelSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            labelMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold),
            ),
        )
}

object AppDarkTheme : AppTheme {
    override val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF067432),
            onPrimary = Color.White,
            secondary = Color(0xFFFDA329),
            onSecondary = Color.White,
            background = Color(0xFF121212), // Dark background color
            onBackground = Color.White,
            surface = Color(0xFF333333), // Dark surface color
            onSurface = Color.White,
            error = Color(0XFFFF0000),
        )

    override val expenseColor = Color(0XFFFD3C4A)
    override val deleteColor = Color(0XFFFD3C4A)
    override val updateColor = Color(0xFF2196F3)
    override val incomeColor = Color(0XFF067432)
    override val transferColor = Color(0xFFFDD835)
    override val cardColor = Color(0xFF1E1E1E) // Dark card color
    override val secondaryTextColor = Color(0xFFAAAAAA) // Adjusted secondary text color
    override val textFieldIndicatorColor = Color(0xFF667C8C) // Adjusted text field indicator color
    override val grayColor = Color(0xFF777777) // Adjusted gray color

    override val typography: Typography
        @Composable
        get() = MaterialTheme.typography.copy(
            displayLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            displayMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            displaySmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            headlineLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            headlineMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            headlineSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            titleLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            titleMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)

            ),
            titleSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.medium)
            ),
            bodyLarge = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)
            ),
            bodyMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.regular)

            ),
            labelLarge = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)

            ),
            bodySmall = TextStyle(
                color = secondaryTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.medium)

            ),
            labelSmall = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold)
            ),
            labelMedium = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = fontFamilyResource(MR.fonts.Tajawal.bold),
            ),
        )
}