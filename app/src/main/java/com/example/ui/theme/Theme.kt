package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkNeonColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color(0xFF03141F),
  primaryContainer = Color(0xFF003846),
  onPrimaryContainer = NeonCyanLight,
  secondary = ElectricBlue,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF0A2552),
  onSecondaryContainer = Color(0xFFC7DCFF),
  tertiary = NeonTeal,
  onTertiary = Color(0xFF00201A),
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder,
  outlineVariant = DarkBorderGlow,
  error = AccentError,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkNeonColorScheme,
    typography = Typography,
    content = content
  )
}
