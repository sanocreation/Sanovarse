package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = CinePrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF3B0B0E),
  onPrimaryContainer = Color(0xFFFFDAD9),
  secondary = CineSecondary,
  onSecondary = Color.Black,
  tertiary = CineTertiary,
  background = CineBg,
  onBackground = CineTextPrimary,
  surface = CineSurface,
  onSurface = CineTextPrimary,
  surfaceVariant = CineSurfaceVariant,
  onSurfaceVariant = CineTextSecondary,
  outline = CineBorder,
  surfaceContainer = CineSurfaceElevated
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek OLED dark mode
  dynamicColor: Boolean = false, // Keep intentional CineVerse brand colors
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

