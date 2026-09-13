package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// The app bar keeps the same dark surface in both themes.
val TopBarColor = Color(0xFF23282E)
val OnTopBarColor = Color(0xFFF2F3F5)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEFF1F5),
    onSecondaryContainer = Color(0xFF44506B),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF14161A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14161A),
    surfaceVariant = Color(0xFFF3F4F7),
    onSurfaceVariant = Color(0xFF6B7280),
    // m5/base/floating
    surfaceContainer = Color(0xFFFBFCFE),
    outline = Color(0xFFC9CDD6),
    outlineVariant = Color(0xFFEBEEF5),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF20222A),
    onSecondaryContainer = Color(0xFFC2C7D0),
    background = Color(0xFF16191D),
    onBackground = Color(0xFFF2F3F5),
    surface = Color(0xFF1C2026),
    onSurface = Color(0xFFF2F3F5),
    surfaceVariant = Color(0xFF262B33),
    onSurfaceVariant = Color(0xFF8A8F98),
    // m5/base/floating
    surfaceContainer = Color(0xFF23282E),
    outline = Color(0xFF3A3E46),
    outlineVariant = Color(0xFF32373E),
)

@Composable
fun Quiz_AnswerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
