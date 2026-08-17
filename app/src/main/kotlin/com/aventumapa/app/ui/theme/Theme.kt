package com.aventumapa.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AtlasNocturnoColors = darkColorScheme(
    primary = CyanElectric,
    onPrimary = DeepNightBlue,
    primaryContainer = CardRaised,
    onPrimaryContainer = IceWhite,
    secondary = WarmCoral,
    onSecondary = DeepNightBlue,
    tertiary = SunYellow,
    onTertiary = DeepNightBlue,
    background = AtlasNight,
    onBackground = IceWhite,
    surface = CardNavy,
    onSurface = IceWhite,
    surfaceVariant = CardRaised,
    onSurfaceVariant = IceWhite,
    outline = Color(0xFF1B5272),
    error = ErrorRed,
)

private val AventuTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 23.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
    ),
)

private val AventuShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

@Composable
fun AventuMapaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AtlasNocturnoColors,
        typography = AventuTypography,
        shapes = AventuShapes,
        content = content,
    )
}
