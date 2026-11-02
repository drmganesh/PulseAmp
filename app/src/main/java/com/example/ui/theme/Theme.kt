package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CarbonBlack,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = ElectricViolet,
    tertiary = HiResGold,
    onTertiary = CarbonBlack,
    background = CarbonBlack,
    onBackground = TextPrimary,
    surface = DeepCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = NeonPink
)

@Composable
fun MyApplicationTheme(
    accentColor: Color = NeonCyan,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val scheme = DarkColorScheme.copy(
        primary = accentColor,
        onPrimaryContainer = accentColor
    )
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
