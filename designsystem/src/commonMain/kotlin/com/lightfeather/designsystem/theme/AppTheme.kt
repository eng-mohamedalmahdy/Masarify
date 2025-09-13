package com.lightfeather.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.designsystem.theme.AppTheme.shapes
import com.lightfeather.designsystem.theme.AppTheme.typography
import masarify.designsystem.generated.resources.NotoSansArabic_Black
import masarify.designsystem.generated.resources.NotoSansArabic_Bold
import masarify.designsystem.generated.resources.NotoSansArabic_ExtraBold
import masarify.designsystem.generated.resources.NotoSansArabic_ExtraLight
import masarify.designsystem.generated.resources.NotoSansArabic_Light
import masarify.designsystem.generated.resources.NotoSansArabic_Medium
import masarify.designsystem.generated.resources.NotoSansArabic_Regular
import masarify.designsystem.generated.resources.NotoSansArabic_SemiBold
import masarify.designsystem.generated.resources.NotoSansArabic_Thin
import masarify.designsystem.generated.resources.NotoSans_Black
import masarify.designsystem.generated.resources.NotoSans_Bold
import masarify.designsystem.generated.resources.NotoSans_ExtraBold
import masarify.designsystem.generated.resources.NotoSans_ExtraLight
import masarify.designsystem.generated.resources.NotoSans_Light
import masarify.designsystem.generated.resources.NotoSans_Medium
import masarify.designsystem.generated.resources.NotoSans_Regular
import masarify.designsystem.generated.resources.NotoSans_SemiBold
import masarify.designsystem.generated.resources.NotoSans_Thin
import masarify.designsystem.generated.resources.Res
import org.jetbrains.compose.resources.Font

object AppTheme {
    object colors {
        // Masarify Base Palette
        val primary = Color(0xFF055250) // Deep Teal
        val secondary = Color(0xFFD9B382) // Muted Beige-Gold

        // Background & Surface
        val backgroundLight = Color(0xFFFFFFFF) // White
        val backgroundDark = Color(0xFF121212) // Dark background
        val surfaceLight = Color(0xFFFDFDFC) // Off-white, softer than pure white
        val surfaceDark = Color(0xFF1C1C1C) // Dark neutral
        val surfaceVariantLight = Color(0xFFE6E1D5) // Warm grey with beige tone
        val surfaceVariantDark = Color(0xFF49453D) // Muted warm charcoal

        // Outline & Divider
        val outlineLight = Color(0xFF8D8D8D)
        val outlineDark = Color(0xFF9E9E9E)

        // Feedback Colors
        val success = Color(0xFF4CAF50) // Green
        val error = Color(0xFFCF6679) // Red/Pink
        val warning = Color(0xFFFFA726) // Orange
        val info = Color(0xFF42A5F5) // Blue (optional, fintech clarity)

        // Tonal Variants
        val primaryContainerLight = Color(0xFFB2DFDB) // Soft teal tint
        val primaryContainerDark = Color(0xFF003D39) // Deep teal
        val secondaryContainerLight = Color(0xFFFFE0B2) // Soft golden beige
        val secondaryContainerDark = Color(0xFF5C4630) // Dark gold-brown

        internal val light =
            lightColorScheme(
                primary = primary,
                onPrimary = Color.White,
                primaryContainer = primaryContainerLight,
                onPrimaryContainer = Color(0xFF00201D),
                secondary = secondary,
                onSecondary = Color(0xFF1C1B17),
                secondaryContainer = secondaryContainerLight,
                onSecondaryContainer = Color(0xFF251A00),
                background = backgroundLight,
                onBackground = Color(0xFF1C1B1F),
                surface = surfaceLight,
                onSurface = Color(0xFF1C1B1F),
                surfaceVariant = surfaceVariantLight,
                onSurfaceVariant = Color(0xFF49454F),
                error = error,
                onError = Color.White,
                outline = outlineLight,
            )

        internal val dark =
            darkColorScheme(
                primary = primary,
                onPrimary = Color(0xFF003732),
                primaryContainer = primaryContainerDark,
                onPrimaryContainer = Color(0xFFB2DFDB),
                secondary = secondary,
                onSecondary = Color(0xFF382E1F),
                secondaryContainer = secondaryContainerDark,
                onSecondaryContainer = Color(0xFFFFE0B2),
                background = backgroundDark,
                onBackground = Color(0xFFEDEDED),
                surface = surfaceDark,
                onSurface = Color(0xFFF5F5F5),
                surfaceVariant = surfaceVariantDark,
                onSurfaceVariant = Color(0xFFCAC4D0),
                error = error,
                onError = Color.Black,
                outline = outlineDark,
            )
    }

    private val NotoSansArabic
        @Composable get() =
            FontFamily(
                Font(Res.font.NotoSansArabic_Thin, FontWeight.Thin),
                Font(Res.font.NotoSansArabic_ExtraLight, FontWeight.ExtraLight),
                Font(Res.font.NotoSansArabic_Light, FontWeight.Light),
                Font(Res.font.NotoSansArabic_Regular, FontWeight.Normal),
                Font(Res.font.NotoSansArabic_Medium, FontWeight.Medium),
                Font(Res.font.NotoSansArabic_SemiBold, FontWeight.SemiBold),
                Font(Res.font.NotoSansArabic_Bold, FontWeight.Bold),
                Font(Res.font.NotoSansArabic_ExtraBold, FontWeight.ExtraBold),
                Font(Res.font.NotoSansArabic_Black, FontWeight.Black),
            )

    private val NotoSans
        @Composable get() =
            FontFamily(
                Font(Res.font.NotoSans_Thin, FontWeight.Thin),
                Font(Res.font.NotoSans_ExtraLight, FontWeight.ExtraLight),
                Font(Res.font.NotoSans_Light, FontWeight.Light),
                Font(Res.font.NotoSans_Regular, FontWeight.Normal),
                Font(Res.font.NotoSans_Medium, FontWeight.Medium),
                Font(Res.font.NotoSans_SemiBold, FontWeight.SemiBold),
                Font(Res.font.NotoSans_Bold, FontWeight.Bold),
                Font(Res.font.NotoSans_ExtraBold, FontWeight.ExtraBold),
                Font(Res.font.NotoSans_Black, FontWeight.Black),
            )

    /**
     * Adaptive font family that automatically switches between Arabic and Latin fonts
     * based on the current layout direction (RTL/LTR)
     */
    val AppFontFamily
        @Composable get() =
            when (LocalLayoutDirection.current) {
                LayoutDirection.Rtl -> NotoSansArabic
                LayoutDirection.Ltr -> NotoSans
            }

    val typography
        @Composable get() =
            Typography(
                displayLarge =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 57.sp,
                        lineHeight = 64.sp,
                        letterSpacing = (-0.25).sp,
                    ),
                displayMedium =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 45.sp,
                        lineHeight = 52.sp,
                    ),
                displaySmall =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 36.sp,
                        lineHeight = 44.sp,
                    ),
                headlineLarge =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                    ),
                headlineMedium =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 28.sp,
                        lineHeight = 30.sp,
                    ),
                headlineSmall =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                    ),
                titleLarge =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        lineHeight = 24.sp,
                    ),
                titleMedium =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 0.1.sp,
                    ),
                titleSmall =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.1.sp,
                    ),
                bodyLarge =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.5.sp,
                    ),
                bodyMedium =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.25.sp,
                    ),
                bodySmall =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.4.sp,
                    ),
                labelLarge =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.1.sp,
                    ),
                labelMedium =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp,
                    ),
                labelSmall =
                    TextStyle(
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp,
                    ),
            )

    val shapes =
        Shapes(
            extraSmall = RoundedCornerShape(dimens.small),
            small = RoundedCornerShape(dimens.medium),
            medium = RoundedCornerShape(dimens.default),
            large = RoundedCornerShape(dimens.large),
            extraLarge = RoundedCornerShape(dimens.extraLarge),
        )

    object dimens {
        // Core Spacing - Base design tokens
        val hairline = 1.dp
        val extraSmall = 2.dp
        val small = 4.dp
        val medium = 8.dp
        val compact = 12.dp
        val default = 16.dp
        val normal = 20.dp
        val large = 24.dp
        val extraLarge = 32.dp
        val tiny = 1.dp
        val huge = 48.dp
        val massive = 64.dp

        object spacing {
            object margin {
                val tiny = 4.dp
                val small = 8.dp
                val medium = 16.dp
                val large = 24.dp
                val xLarge = 32.dp
                val huge = 48.dp

                object context {
                    val form = 16.dp
                    val section = 24.dp
                    val card = 16.dp
                    val list = 8.dp
                    val dialog = 24.dp
                }
            }

            object padding {
                val tiny = 4.dp
                val small = 8.dp
                val medium = 16.dp
                val large = 24.dp
                val xLarge = 32.dp

                object component {
                    val button = 16.dp
                    val card = 16.dp
                    val listItem = 12.dp
                    val chip = 12.dp
                    val bottomSheet = 16.dp
                }
            }

            object layout {
                val grid = 8.dp
                val gridLarge = 16.dp
                val container = 16.dp
                val containerLarge = 24.dp
                val safeArea = 16.dp
            }

            object typography {
                val lineSpacingTight = 4.dp
                val lineSpacingNormal = 8.dp
                val lineSpacingLoose = 12.dp

                val textMarginSmall = 4.dp
                val textMarginMedium = 8.dp
                val textMarginLarge = 16.dp
            }
        }

        object elevation {
            val level0 = 0.dp
            val level1 = 1.dp
            val level2 = 3.dp
            val level3 = 6.dp
            val level4 = 8.dp
            val level5 = 12.dp

            object component {
                val card = level1
                val sheet = level4
                val dialog = level3
                val fab = level3
                val appBar = level0
                val menu = level2
            }
        }

        object icon {
            object size {
                val xSmall = 12.dp
                val small = 16.dp
                val medium = 24.dp
                val large = 32.dp
                val xLarge = 48.dp
                val xxLarge = 64.dp
            }

            object context {
                val toolbar = 24.dp
                val tab = 24.dp
                val avatar = 40.dp
                val logo = 56.dp
                val button = 18.dp
                val listItem = 24.dp
            }
        }

        object border {
            val thin = 1.dp
            val medium = 2.dp
            val thick = 4.dp
            val focus = 2.dp
            val error = 2.dp
        }

        object radius {
            val xSmall = 2.dp
            val small = 4.dp
            val medium = 8.dp
            val large = 12.dp
            val xLarge = 16.dp
            val circular = 50.dp
        }

        object component {
            object button {
                val height = 48.dp
                val heightSmall = 32.dp
                val heightLarge = 56.dp
                val minWidth = 64.dp
            }

            object textField {
                val height = 56.dp
                val heightSmall = 40.dp
                val heightLarge = 64.dp
            }

            object appBar {
                val height = 56.dp
            }

            object tab {
                val height = 48.dp
            }

            object bottomNav {
                val height = 80.dp
            }

            object listItem {
                val height = 56.dp
                val heightSmall = 40.dp
                val heightLarge = 72.dp
            }

            object card {
                val minHeight = 120.dp
                val maxWidth = 400.dp
            }

            object dialog {
                val maxWidth = 560.dp
                val minWidth = 280.dp
            }
        }

        object touchTarget {
            val min = 48.dp
            val comfortable = 56.dp
            val button = 48.dp
            val icon = 48.dp
        }

        object motion {
            val swipeThreshold = 56.dp
            val dragHandle = 32.dp
            val dragHandleHeight = 4.dp
        }
    }
}

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
//    dynamicColor: Boolean = true, // support dynamic colors
    content: @Composable () -> Unit,
) {
    val isPreview = LocalInspectionMode.current
    val colorScheme =
        when {
            isPreview -> if (useDarkTheme) AppTheme.colors.dark else AppTheme.colors.light
            useDarkTheme -> AppTheme.colors.dark
            else -> AppTheme.colors.light
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (isPreview) typography else responsiveTypography(),
        shapes = shapes,
        content = content,
    )
}
