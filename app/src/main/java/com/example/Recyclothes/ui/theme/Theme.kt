package com.example.Recyclothes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary      = MediumBlue,
    onPrimary    = Color.White,
    secondary    = TealMedium,
    onSecondary  = Color.White,
    background   = DeepBlue,
    onBackground = AquaLight,
    surface      = TealDark,
    onSurface    = AquaLight
)

private val LightColorScheme = lightColorScheme(
    primary      = MediumBlue,
    onPrimary    = Color.White,
    secondary    = TealMedium,
    onSecondary  = Color.White,
    background   = SoftBlue,
    onBackground = DeepBlue,
    surface      = AquaLight,
    onSurface    = DeepBlue
)

/**
 * Tema principal de la app con soporte para:
 * - Dynamic color en Android 12+ (Material You)
 * - Paleta personalizada para light/dark
 */
@Composable
fun RecyclothesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
