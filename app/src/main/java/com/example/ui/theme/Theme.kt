package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.downloader.FontManager

private val Web2ApkDarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryIndigoDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = AccentCyanLight,
    tertiary = AccentEmerald,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderHighlight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Cyberpunk dark slate theme is optimized for Web2APK builder
    content: @Composable () -> Unit
) {
    val currentFontFamily = FontManager.fontFamilyState.value
    val typography = getAppTypography(currentFontFamily)

    MaterialTheme(
        colorScheme = Web2ApkDarkColorScheme,
        typography = typography,
        content = content
    )
}
