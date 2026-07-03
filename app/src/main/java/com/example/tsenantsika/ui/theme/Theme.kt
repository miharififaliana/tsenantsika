package com.example.tsenantsika.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    tertiary = AlertRed,
    background = BackgroundWhite,
    surface = BackgroundLight,
    onBackground = TextDark,
    onSurface = TextDark,
    error = AlertRed
)

@Composable
fun TsenantsikaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
