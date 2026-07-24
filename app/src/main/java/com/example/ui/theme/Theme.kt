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
import com.example.data.preferences.AccentColor
import com.example.data.preferences.AppThemeMode

@Composable
fun SocialHubTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.PURPLE,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = accentColor.primaryColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.darkContainerColor,
            onPrimaryContainer = accentColor.darkOnContainerColor,
            secondary = accentColor.primaryColor,
            onSecondary = Color.White,
            tertiary = BentoInstagramContainer,
            background = Color(0xFF141218),
            surface = Color(0xFF211F26),
            onBackground = Color(0xFFE6E0E9),
            onSurface = Color(0xFFE6E0E9),
            surfaceVariant = Color(0xFF36343B),
            onSurfaceVariant = Color(0xFFCAC4D0)
        )
        else -> lightColorScheme(
            primary = accentColor.primaryColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.lightContainerColor,
            onPrimaryContainer = accentColor.lightOnContainerColor,
            secondary = accentColor.primaryColor,
            onSecondary = Color.White,
            tertiary = BentoInstagramText,
            background = BentoCanvas,
            surface = Color.White,
            onBackground = BentoTextPrimary,
            onSurface = BentoTextPrimary,
            surfaceVariant = BentoSurface,
            onSurfaceVariant = BentoTextSecondary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
