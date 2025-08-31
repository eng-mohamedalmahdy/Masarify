package com.lightfeather.designsystem


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import masarify.designsystem.generated.resources.NotoSansArabic_Black
import masarify.designsystem.generated.resources.NotoSansArabic_Bold
import masarify.designsystem.generated.resources.NotoSansArabic_ExtraBold
import masarify.designsystem.generated.resources.NotoSansArabic_ExtraLight
import masarify.designsystem.generated.resources.NotoSansArabic_Light
import masarify.designsystem.generated.resources.NotoSansArabic_Medium
import masarify.designsystem.generated.resources.NotoSansArabic_Regular
import masarify.designsystem.generated.resources.NotoSansArabic_SemiBold
import masarify.designsystem.generated.resources.NotoSansArabic_Thin
import masarify.designsystem.generated.resources.Res
import org.jetbrains.compose.resources.Font

object AppTheme {

    object colors {
        // Masarify Base Palette
        val primary = Color(0xFF1E3A8A) // Deep Royal Blue
        val secondary = Color(0xFF2563EB) // Vivid Blue
        val backgroundLight = Color(0xFFFFFFFF) // White
        val backgroundDark = Color(0xFF121212) // Dark background
        val surfaceLight = Color(0xFFF5F5F5) // Light Grey
        val surfaceDark = Color(0xFF1E1E1E) // Dark Grey
        val surfaceVariantLight = Color(0xFFE0E0E0) // Neutral Grey
        val surfaceVariantDark = Color(0xFF2C2C2C) // Charcoal Grey

        // Feedback Colors
        val success = Color(0xFF4CAF50)
        val error = Color(0xFFCF6679)
        val warning = Color(0xFFFFA726)

        internal val light = lightColorScheme(
            primary = primary,
            secondary = secondary,
            background = backgroundLight,
            surface = surfaceLight,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            surfaceVariant = surfaceVariantLight,
            error = error,
        )

        internal val dark = darkColorScheme(
            primary = Color(0xFF93C5FD), // lighter for contrast
            secondary = Color(0xFF60A5FA),
            background = backgroundDark,
            surface = surfaceDark,
            onPrimary = Color(0xFF0B1120),
            onSecondary = Color(0xFF0B1120),
            onBackground = Color(0xFFEDEDED),
            onSurface = Color(0xFFF5F5F5),
            surfaceVariant = surfaceVariantDark,
            error = error,
        )
    }

    private val NotoSansArabic
        @Composable get() = FontFamily(
            Font(Res.font.NotoSansArabic_Thin, FontWeight.Thin),
            Font(Res.font.NotoSansArabic_ExtraLight, FontWeight.ExtraLight),
            Font(Res.font.NotoSansArabic_Light, FontWeight.Light),
            Font(Res.font.NotoSansArabic_Regular, FontWeight.Normal),
            Font(Res.font.NotoSansArabic_Medium, FontWeight.Medium),
            Font(Res.font.NotoSansArabic_SemiBold, FontWeight.SemiBold),
            Font(Res.font.NotoSansArabic_Bold, FontWeight.Bold),
            Font(Res.font.NotoSansArabic_ExtraBold, FontWeight.ExtraBold),
            Font(Res.font.NotoSansArabic_Black, FontWeight.Black)
        )

    val typography
        @Composable get() = Typography(
            displayLarge = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Light,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp
            ),
            displayMedium = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Light,
                fontSize = 45.sp,
                lineHeight = 52.sp
            ),
            displaySmall = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 44.sp
            ),
            headlineLarge = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                lineHeight = 36.sp
            ),
            headlineSmall = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            titleLarge = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 28.sp
            ),
            titleMedium = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.1.sp
            ),
            titleSmall = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp
            ),
            labelLarge = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontFamily = NotoSansArabic,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp
            )
        )


    val shapes = Shapes(
        extraSmall = RoundedCornerShape(dimens.extraSmall),
        small = RoundedCornerShape(dimens.small),
        medium = RoundedCornerShape(dimens.medium),
        large = RoundedCornerShape(dimens.large),
        extraLarge = RoundedCornerShape(dimens.extraLarge)
    )

    object dimens {
        // Core Spacing
        val hairline = 1.dp
        val extraSmall = 2.dp
        val small = 4.dp
        val medium = 8.dp
        val compact = 12.dp
        val default = 16.dp
        val normal = 20.dp
        val large = 24.dp
        val extraLarge = 32.dp

        // Extended Sizes
        val marginSmall = 8.dp
        val marginMedium = 16.dp
        val marginLarge = 24.dp

        val paddingSmall = 8.dp
        val paddingMedium = 16.dp
        val paddingLarge = 24.dp

        val iconSizeSmall = 16.dp
        val iconSizeMedium = 24.dp
        val iconSizeLarge = 32.dp

        val buttonHeight = 48.dp
        val textFieldHeight = 56.dp
        val cardElevation = 4.dp
        val sheetElevation = 8.dp
    }

    @Composable
    fun AppTheme(
        useDarkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = true, // support dynamic colors
        content: @Composable () -> Unit
    ) {

        val colorScheme = when {
            dynamicColor -> dynamicColorScheme(
                isDark = useDarkTheme,
                fallback = if (useDarkTheme) AppTheme.colors.dark else AppTheme.colors.light,
            )
            useDarkTheme -> colors.light
            else -> colors.dark

        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}