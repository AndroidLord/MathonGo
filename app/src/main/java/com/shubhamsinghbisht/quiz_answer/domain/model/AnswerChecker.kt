package com.shubhamsinghbisht.quiz_answer.domain.model

/**
 * Answer validation, kept out of the UI so it can be unit tested without Compose.
 *
 * Every decision is derived from the question's own data; nothing about specific ids, options or
 * values is hardcoded here.
 */
object AnswerChecker {

    /** True when [selectedOptionIds] is a winning answer for [question]. */
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

    /** Guards against ids that do not belong to this question at all. */
    fun areValidOptions(question: Question, selectedOptionIds: Set<String>): Boolean {
        val known = question.options.mapTo(mutableSetOf()) { it.id }
        return known.containsAll(selectedOptionIds)
    }

    /**
     * Checks a typed numerical answer. Uses the inclusive [NumericalAnswer.lowerLimit] /
     * [NumericalAnswer.upperLimit] range when both are present, otherwise compares against
     * [NumericalAnswer.correctValue]. Unparseable input is simply wrong, never an exception.
     */
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

    /** Absorbs binary floating point noise; the source limits carry at most one decimal place. */
    private const val EPSILON = 1e-6
}
