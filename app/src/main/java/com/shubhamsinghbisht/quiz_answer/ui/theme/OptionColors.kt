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
    val emphasized: Boolean,
    val label: String?,
    val labelIcon: ImageVector?,
    val labelColor: Color,
)

@Composable
fun optionColors(answerState: QuestionAnswerState, optionId: String): OptionColors {
    val scheme = MaterialTheme.colorScheme
    val palette = if (LocalIsDarkTheme.current) DarkFeedback else LightFeedback

    val neutral = OptionColors(
        container = scheme.surface,
        border = scheme.outline,
        content = scheme.onSurface,
        badgeContainer = Color.Transparent,
        badgeContent = scheme.onSurfaceVariant,
        emphasized = false,
        label = null,
        labelIcon = null,
        labelColor = scheme.onSurfaceVariant,
    )

    fun accented(
        accent: Color,
        container: Color,
        label: String?,
        labelIcon: ImageVector?,
    ) = OptionColors(
        container = container,
        border = accent,
        content = scheme.onSurface,
        badgeContainer = accent,
        badgeContent = palette.onAccent,
        emphasized = true,
        label = label,
        labelIcon = labelIcon,
        labelColor = accent,
    )

    return when (answerState) {
        QuestionAnswerState.Unanswered -> neutral

        is QuestionAnswerState.Selected ->
            if (optionId in answerState.selectedOptionIds) {
                accented(palette.selected, palette.selectedContainer, label = null, labelIcon = null)
            } else {
                neutral
            }

        // Checked states keep the plain card fill: only the border, badge and label carry colour.
        is QuestionAnswerState.CheckedCorrect ->
            if (optionId in answerState.selectedOptionIds) {
                accented(palette.correct, scheme.surface, "Correct Answer", Icons.Default.Check)
            } else {
                neutral
            }

        is QuestionAnswerState.CheckedIncorrect -> when (optionId) {
            in answerState.correctOptionIds ->
                accented(palette.correct, scheme.surface, "Correct Answer", Icons.Default.Check)

            in answerState.selectedOptionIds ->
                accented(palette.incorrect, scheme.surface, "Your Answer", Icons.Default.Close)

            else -> neutral
        }
    }
}
