package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionAnswerState

@Immutable
data class OptionColors(
    val container: Color,
    val border: Color,
    val content: Color,
    val badgeContainer: Color,
    val badgeContent: Color,
    val badgeIcon: ImageVector?,
    val emphasized: Boolean,
)

@Composable
fun optionColors(answerState: QuestionAnswerState, optionId: String): OptionColors {
    val scheme = MaterialTheme.colorScheme
    val palette = if (LocalIsDarkTheme.current) DarkFeedback else LightFeedback

    val neutral = OptionColors(
        container = scheme.surface,
        border = scheme.outlineVariant,
        content = scheme.onSurface,
        badgeContainer = scheme.surfaceVariant,
        badgeContent = scheme.onSurfaceVariant,
        badgeIcon = null,
        emphasized = false,
    )

    fun accented(accent: Color, container: Color, icon: ImageVector?) = OptionColors(
        container = container,
        border = accent,
        content = scheme.onSurface,
        badgeContainer = accent,
        badgeContent = palette.onAccent,
        badgeIcon = icon,
        emphasized = true,
    )

    return when (answerState) {
        QuestionAnswerState.Unanswered -> neutral

        is QuestionAnswerState.Selected ->
            if (optionId in answerState.selectedOptionIds) {
                accented(palette.selected, palette.selectedContainer, icon = null)
            } else {
                neutral
            }

        is QuestionAnswerState.CheckedCorrect ->
            if (optionId in answerState.selectedOptionIds) {
                accented(palette.correct, palette.correctContainer, Icons.Default.Check)
            } else {
                neutral
            }

        is QuestionAnswerState.CheckedIncorrect -> when (optionId) {
            in answerState.correctOptionIds ->
                accented(palette.correct, palette.correctContainer, Icons.Default.Check)
            in answerState.selectedOptionIds ->
                accented(palette.incorrect, palette.incorrectContainer, Icons.Default.Close)
            else -> neutral
        }
    }
}
