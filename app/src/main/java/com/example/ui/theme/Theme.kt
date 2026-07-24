package com.example.ui.theme

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
    primary = BentoPillActive,
    onPrimary = BentoPrimaryAccent,
    secondary = BentoXContainer,
    onSecondary = BentoXText,
    tertiary = BentoInstagramContainer,
    background = Color(0xFF141218),
    surface = Color(0xFF211F26),
    onBackground = Color(0xFFE6E0E9),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    primaryContainer = Color(0xFF3B2D54),
    onPrimaryContainer = BentoPillActive
)

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimaryAccent,
    onPrimary = Color.White,
    secondary = BentoWhatsAppText,
    onSecondary = Color.White,
    tertiary = BentoInstagramText,
    background = BentoCanvas,
    surface = Color.White,
    onBackground = BentoTextPrimary,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoSurface,
    onSurfaceVariant = BentoTextSecondary,
    primaryContainer = BentoPillActive,
    onPrimaryContainer = BentoPrimaryAccent
)

@Composable
fun SocialHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
