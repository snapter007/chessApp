package com.chessforge.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChessForgeDarkScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = CorrectGreen,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    error = WrongRed,
)

private val ChessForgeLightScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = CorrectGreen,
)

@Composable
fun ChessForgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // The trainer is designed around a dark board; we always render dark for
    // visual consistency, matching the professional look of most chess apps.
    val colorScheme = if (darkTheme) ChessForgeDarkScheme else ChessForgeDarkScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChessForgeTypography,
        content = content,
    )
}
