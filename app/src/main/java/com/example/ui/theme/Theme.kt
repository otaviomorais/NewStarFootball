package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SoccerColorScheme = darkColorScheme(
  primary = StadiumGreenLight,
  onPrimary = Color.White,
  primaryContainer = StadiumGreenDark,
  onPrimaryContainer = ChalkWhite,
  secondary = ChampionGold,
  onSecondary = BallBlack,
  secondaryContainer = GoldDark,
  onSecondaryContainer = Color.White,
  tertiary = EnergyBlue,
  onTertiary = Color.White,
  background = SurfaceDark,
  onBackground = TextPrimary,
  surface = SurfaceCard,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceCardLight,
  onSurfaceVariant = TextSecondary,
  outline = TextMuted,
  error = CardRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SoccerColorScheme,
    typography = Typography,
    content = content
  )
}

