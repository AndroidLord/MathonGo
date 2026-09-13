package com.shubhamsinghbisht.quiz_answer.domain.model

object AnswerChecker {

    fun isCorrect(question: Question, selectedOptionIds: Set<String>): Boolean {
        if (selectedOptionIds.isEmpty()) return false
        if (!areValidOptions(question, selectedOptionIds)) return false
        return when (question.type) {
            QuestionType.SINGLE_CORRECT ->
                selectedOptionIds.size == 1 && selectedOptionIds.single() in question.correctOptionIds
            QuestionType.MULTIPLE_CORRECT ->
                selectedOptionIds == question.correctOptionIds
            QuestionType.NUMERICAL, QuestionType.UNKNOWN -> false
        }
    }

    fun areValidOptions(question: Question, selectedOptionIds: Set<String>): Boolean {
        val known = question.options.mapTo(mutableSetOf()) { it.id }
        return known.containsAll(selectedOptionIds)
    }

    fun isNumericalCorrect(question: Question, input: String): Boolean {
        val answer = question.numericalAnswer ?: return false
        val value = input.trim().toDoubleOrNull() ?: return false
        if (!value.isFinite()) return false

        val lower = answer.lowerLimit
        val upper = answer.upperLimit
        if (lower != null && upper != null) {
            val lo = minOf(lower, upper)
            val hi = maxOf(lower, upper)
            return value >= lo - EPSILON && value <= hi + EPSILON
        }
        val expected = answer.correctValue ?: return false
        return kotlin.math.abs(value - expected) <= EPSILON
    }

    private const val EPSILON = 1e-6
}
