package com.example.movieapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary        = Blue900,
    onPrimary      = White,
    background     = Black,
    onBackground   = White,
    surface        = DarkSurface,
    onSurface      = White,
    surfaceVariant = DarkCard,
    secondary      = Blue300,
    onSecondary    = Black,
    error          = ErrorColor
)

private val LightColorScheme = lightColorScheme(
    primary        = Blue900,
    onPrimary      = White,
    background     = LightBg,
    onBackground   = Black,
    surface        = White,
    onSurface      = Black,
    surfaceVariant = Color(0xFFE3F2FD),
    secondary      = Blue500,
    onSecondary    = White,
    error          = ErrorColor
)

@Composable
fun MovieAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}