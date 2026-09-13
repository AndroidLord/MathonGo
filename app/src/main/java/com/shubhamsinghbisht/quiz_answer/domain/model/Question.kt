package com.shubhamsinghbisht.quiz_answer.domain.model

enum class QuestionType {
    SINGLE_CORRECT,
    MULTIPLE_CORRECT,
    NUMERICAL,
    UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): QuestionType = when (raw) {
            "singleCorrect" -> SINGLE_CORRECT
            "multipleCorrect" -> MULTIPLE_CORRECT
            "numerical" -> NUMERICAL
            else -> UNKNOWN
        }
    }
}

data class Option(
    val id: String,
    val contentHtml: String,
    val imageUrl: String?,
    val isCorrect: Boolean,
)

data class NumericalAnswer(
    val correctValue: Double?,
    val rawValue: String?,
    val lowerLimit: Double?,
    val upperLimit: Double?,
)

data class Question(
    val id: String,
    val type: QuestionType,
    val contentHtml: String,
    val imageUrl: String?,
    val options: List<Option>,
    val examTitle: String?,
    val subjectTitle: String?,
    val chapterTitle: String?,
    val previousYearPapers: List<String>,
    val hasVideoSolution: Boolean,
    val numericalAnswer: NumericalAnswer?,
) {
    val correctOptionIds: Set<String>
        get() = options.filter { it.isCorrect }.map { it.id }.toSet()
}

data class QuestionBank(
    val examTitle: String?,
    val questions: List<Question>,
) {
    fun supporting(types: Set<QuestionType>): QuestionBank =
        copy(questions = questions.filter { it.type in types })
}
