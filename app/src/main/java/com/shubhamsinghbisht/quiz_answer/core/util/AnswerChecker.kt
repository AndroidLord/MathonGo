package com.shubhamsinghbisht.quiz_answer.core.util

import com.shubhamsinghbisht.quiz_answer.domain.model.Question
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import kotlin.math.abs

object AnswerChecker {

    private const val EPSILON = 1e-6

    fun isCorrect(question: Question, selectedOptionIds: Set<String>): Boolean {
        if (selectedOptionIds.isEmpty()) return false
        if (!areValidOptions(question, selectedOptionIds)) return false
        return when (question.type) {
            QuestionType.SINGLE_CORRECT ->
                selectedOptionIds.size == 1 &&
                    selectedOptionIds.single() in question.correctOptionIds
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
            return value >= minOf(lower, upper) - EPSILON && value <= maxOf(lower, upper) + EPSILON
        }
        val expected = answer.correctValue ?: return false
        return abs(value - expected) <= EPSILON
    }
}
