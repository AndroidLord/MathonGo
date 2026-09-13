package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

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
    outline = Color(0xFFC9CDD6),
    outlineVariant = Color(0xFFE5E7EB),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF20222A),
    onSecondaryContainer = Color(0xFFC2C7D0),
    background = Color(0xFF0B0B0D),
    onBackground = Color(0xFFF2F3F5),
    surface = Color(0xFF131419),
    onSurface = Color(0xFFF2F3F5),
    surfaceVariant = Color(0xFF1E2027),
    onSurfaceVariant = Color(0xFF8A8F98),
    outline = Color(0xFF3A3E46),
    outlineVariant = Color(0xFF26282E),
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
