package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = IndigoPrimaryDark,
        onPrimary = IndigoOnPrimaryDark,
        primaryContainer = IndigoPrimaryContainerDark,
        onPrimaryContainer = IndigoOnPrimaryContainerDark,
        secondary = SlateSecondaryDark,
        onSecondary = SlateOnSecondaryDark,
        secondaryContainer = SlateSecondaryContainerDark,
        onSecondaryContainer = SlateOnSecondaryContainerDark,
        tertiary = AmberTertiaryDark,
        onTertiary = AmberOnTertiaryDark,
        tertiaryContainer = AmberTertiaryContainerDark,
        onTertiaryContainer = AmberOnTertiaryContainerDark,
        background = BackgroundDark,
        onBackground = OnSurfaceDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceContainerDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark
    )

private val LightColorScheme =
    lightColorScheme(
        primary = IndigoPrimary,
        onPrimary = IndigoOnPrimary,
        primaryContainer = IndigoPrimaryContainer,
        onPrimaryContainer = IndigoOnPrimaryContainer,
        secondary = SlateSecondary,
        onSecondary = SlateOnSecondary,
        secondaryContainer = SlateSecondaryContainer,
        onSecondaryContainer = SlateOnSecondaryContainer,
        tertiary = AmberTertiary,
        onTertiary = AmberOnTertiary,
        tertiaryContainer = AmberTertiaryContainer,
        onTertiaryContainer = AmberOnTertiaryContainer,
        background = BackgroundLight,
        onBackground = OnSurfaceLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceContainerLight,
        onSurfaceVariant = OnSurfaceVariantLight,
        outline = OutlineLight,
        outlineVariant = OutlineVariantLight
    )

@Composable
fun AssetOpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Fixed industrial theme for consistent contrast
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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AssetOpsTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
