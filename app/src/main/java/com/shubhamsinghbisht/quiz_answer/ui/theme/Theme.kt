package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2F6BFF),
    onPrimary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDEFF4),
    onSecondaryContainer = Color(0xFF44506B),
    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF15181D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF15181D),
    surfaceVariant = Color(0xFFEDEFF4),
    onSurfaceVariant = Color(0xFF5B6472),
    outline = Color(0xFFC3C9D4),
    outlineVariant = Color(0xFFDFE3EA),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7BA4FF),
    onPrimary = Color(0xFF0E1116),
    secondaryContainer = Color(0xFF232A36),
    onSecondaryContainer = Color(0xFFBFC8D9),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE8ECF3),
    surface = Color(0xFF161A21),
    onSurface = Color(0xFFE8ECF3),
    surfaceVariant = Color(0xFF232A36),
    onSurfaceVariant = Color(0xFF9AA5B6),
    outline = Color(0xFF3A4252),
    outlineVariant = Color(0xFF2A313D),
)

@Composable
fun Quiz_AnswerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
