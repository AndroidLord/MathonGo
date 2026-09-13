package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.shubhamsinghbisht.quiz_answer.presentation.question.OptionVisualState

@Immutable
data class OptionColors(
    val container: Color,
    val border: Color,
    val content: Color,
    val badgeContainer: Color,
    val badgeContent: Color,
)

@Composable
fun optionColors(state: OptionVisualState): OptionColors {
    val scheme = MaterialTheme.colorScheme
    val dark = LocalIsDarkTheme.current
    val palette = if (dark) DarkFeedback else LightFeedback

    return when (state) {
        OptionVisualState.NEUTRAL -> OptionColors(
            container = scheme.surface,
            border = scheme.outlineVariant,
            content = scheme.onSurface,
            badgeContainer = scheme.surfaceVariant,
            badgeContent = scheme.onSurfaceVariant,
        )
        OptionVisualState.SELECTED -> OptionColors(
            container = palette.selectedContainer,
            border = palette.selected,
            content = scheme.onSurface,
            badgeContainer = palette.selected,
            badgeContent = palette.onAccent,
        )
        OptionVisualState.CORRECT -> OptionColors(
            container = palette.correctContainer,
            border = palette.correct,
            content = scheme.onSurface,
            badgeContainer = palette.correct,
            badgeContent = palette.onAccent,
        )
        OptionVisualState.INCORRECT -> OptionColors(
            container = palette.incorrectContainer,
            border = palette.incorrect,
            content = scheme.onSurface,
            badgeContainer = palette.incorrect,
            badgeContent = palette.onAccent,
        )
    }
}
