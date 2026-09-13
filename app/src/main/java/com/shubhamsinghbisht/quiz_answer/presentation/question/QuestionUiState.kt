package com.shubhamsinghbisht.quiz_answer.presentation.question

import com.shubhamsinghbisht.quiz_answer.domain.model.Question
import kotlinx.serialization.Serializable

@Serializable
data class AnswerRecord(
    val selectedOptionIds: Set<String> = emptySet(),
    val numericalInput: String = "",
    val checked: Boolean = false,
) {
    val hasResponse: Boolean get() = selectedOptionIds.isNotEmpty() || numericalInput.isNotBlank()
}

sealed interface QuestionUiState {

    data object Loading : QuestionUiState

    data class Error(val message: String) : QuestionUiState

    data class Ready(
        val examTitle: String?,
        val question: Question,
        val questionNumber: Int,
        val totalQuestions: Int,
        val record: AnswerRecord,
        val answerState: QuestionAnswerState,
    ) : QuestionUiState {

        val isFirst: Boolean get() = questionNumber <= 1
        val isLast: Boolean get() = questionNumber >= totalQuestions
        val canCheck: Boolean get() = answerState is QuestionAnswerState.Selected
        val isChecked: Boolean get() = record.checked

    }
}
