package com.shubhamsinghbisht.quiz_answer.presentation.question

sealed interface QuestionAnswerState {

    data object Unanswered : QuestionAnswerState

    data class Selected(
        val selectedOptionIds: Set<String>,
    ) : QuestionAnswerState

    data class CheckedCorrect(
        val selectedOptionIds: Set<String>,
    ) : QuestionAnswerState

    data class CheckedIncorrect(
        val selectedOptionIds: Set<String>,
        val correctOptionIds: Set<String>,
    ) : QuestionAnswerState
}
