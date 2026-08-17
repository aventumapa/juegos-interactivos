package com.aventumapa.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = ExplorerTealDark,
    onPrimary = Color.White,
    primaryContainer = PaleTeal,
    onPrimaryContainer = NightBlue,
    secondary = WarmCoral,
    onSecondary = NightBlue,
    secondaryContainer = PaleYellow,
    onSecondaryContainer = NightBlue,
    tertiary = SunYellow,
    background = PaperCream,
    onBackground = NightBlue,
    surface = SoftCream,
    onSurface = NightBlue,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6CD8D1),
    onPrimary = DeepNightBlue,
    primaryContainer = Color(0xFF105F5D),
    onPrimaryContainer = Color(0xFFDAFFFC),
    secondary = Color(0xFFFFA99E),
    onSecondary = DeepNightBlue,
    tertiary = SunYellow,
    background = DeepNightBlue,
    onBackground = Color(0xFFF7F2E7),
    surface = Color(0xFF17324D),
    onSurface = Color(0xFFF7F2E7),
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

@Composable
fun AventuMapaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AventuTypography,
        shapes = Shapes(),
        content = content,
    )
}

