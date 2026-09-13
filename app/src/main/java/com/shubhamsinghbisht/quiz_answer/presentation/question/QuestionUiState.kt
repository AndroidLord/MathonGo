package com.shubhamsinghbisht.quiz_answer.presentation.question

import com.shubhamsinghbisht.quiz_answer.domain.model.AnswerChecker
import com.shubhamsinghbisht.quiz_answer.domain.model.Question
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import kotlinx.serialization.Serializable

@Serializable
data class AnswerState(
    val selectedOptionIds: Set<String> = emptySet(),
    val numericalInput: String = "",
    val checked: Boolean = false,
) {
    val hasSelection: Boolean get() = selectedOptionIds.isNotEmpty() || numericalInput.isNotBlank()
}

enum class OptionVisualState {
    NEUTRAL,
    SELECTED,
    CORRECT,
    INCORRECT,
}

sealed interface QuestionUiState {

    data object Loading : QuestionUiState

    data class Error(val message: String) : QuestionUiState

    data class Ready(
        val examTitle: String?,
        val question: Question,
        val questionNumber: Int,
        val totalQuestions: Int,
        val answer: AnswerState,
    ) : QuestionUiState {

        val isFirst: Boolean get() = questionNumber <= 1
        val isLast: Boolean get() = questionNumber >= totalQuestions
        val canCheck: Boolean get() = !answer.checked && answer.hasSelection

        val correctness: Boolean?
            get() = when {
                !answer.checked -> null
                question.type == QuestionType.NUMERICAL ->
                    AnswerChecker.isNumericalCorrect(question, answer.numericalInput)
                else -> AnswerChecker.isCorrect(question, answer.selectedOptionIds)
            }

        fun visualStateOf(optionId: String): OptionVisualState {
            val selected = optionId in answer.selectedOptionIds
            if (!answer.checked) {
                return if (selected) OptionVisualState.SELECTED else OptionVisualState.NEUTRAL
            }
            val isCorrectOption = optionId in question.correctOptionIds
            return when {
                isCorrectOption -> OptionVisualState.CORRECT
                selected -> OptionVisualState.INCORRECT
                else -> OptionVisualState.NEUTRAL
            }
        }
    }
}
