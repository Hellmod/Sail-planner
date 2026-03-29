package com.hellmod.sailplanner.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Sail Planner Brand Palette ─────────────────────────────────────────────
val OceanBlue = Color(0xFF006994)
val DeepNavy = Color(0xFF002147)
val SeaFoam = Color(0xFF4ECDC4)
val SunsetOrange = Color(0xFFFF6B35)
val WaveWhite = Color(0xFFF5F9FC)
val DarkSea = Color(0xFF1A2B3C)
val CoralRed = Color(0xFFE63946)
val GoldenSand = Color(0xFFFFD700)

private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCE5FF),
    onPrimaryContainer = DeepNavy,
    secondary = SeaFoam,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F5F3),
    onSecondaryContainer = Color(0xFF003733),
    tertiary = SunsetOrange,
    onTertiary = Color.White,
    error = CoralRed,
    background = WaveWhite,
    onBackground = DarkSea,
    surface = Color.White,
    onSurface = DarkSea,
    surfaceVariant = Color(0xFFE8F4F8)
)

private val DarkColorScheme = darkColorScheme(
    primary = SeaFoam,
    onPrimary = DeepNavy,
    primaryContainer = OceanBlue,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF70D8D0),
    onSecondary = Color(0xFF003733),
    tertiary = Color(0xFFFFB399),
    onTertiary = Color(0xFF5A1A00),
    error = Color(0xFFFF8589),
    background = DarkSea,
    onBackground = Color.White,
    surface = Color(0xFF1E3246),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF243D52)
)

@Composable
fun SailPlannerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = SailTypography,
        content = content
    )
}
