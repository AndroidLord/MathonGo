package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shubhamsinghbisht.quiz_answer.ui.theme.DarkFeedback
import com.shubhamsinghbisht.quiz_answer.ui.theme.LightFeedback
import com.shubhamsinghbisht.quiz_answer.ui.theme.LocalIsDarkTheme

private val ALLOWED = Regex("^-?\\d*\\.?\\d*$")

@Composable
fun NumericalAnswerInput(
    value: String,
    answerState: QuestionAnswerState,
    expectedAnswer: String?,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = if (LocalIsDarkTheme.current) DarkFeedback else LightFeedback
    val accent = when (answerState) {
        is QuestionAnswerState.CheckedCorrect -> palette.correct
        is QuestionAnswerState.CheckedIncorrect -> palette.incorrect
        is QuestionAnswerState.Selected -> palette.selected
        QuestionAnswerState.Unanswered -> MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Your answer",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = value,
            onValueChange = { typed ->
                val cleaned = typed.trim()
                if (cleaned.isEmpty() || ALLOWED.matches(cleaned)) onValueChange(cleaned)
            },
            enabled = enabled,
            singleLine = true,
            placeholder = { Text("Enter a number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent,
                unfocusedBorderColor = accent,
                disabledBorderColor = accent,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        when (answerState) {
            is QuestionAnswerState.CheckedCorrect -> Text(
                text = "Correct",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.correct,
            )
            is QuestionAnswerState.CheckedIncorrect -> Text(
                text = expectedAnswer?.let { "Incorrect. Correct answer: $it" } ?: "Incorrect",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.incorrect,
            )
            else -> Unit
        }
    }
}
