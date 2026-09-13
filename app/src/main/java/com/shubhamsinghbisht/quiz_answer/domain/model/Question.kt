package com.shubhamsinghbisht.quiz_answer.domain.model

/** Question kinds present in data.json. Anything else maps to [UNKNOWN] rather than failing the load. */
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
    /** Rich content: may hold HTML, entities, LaTeX and MathML, often mixed in one string. */
    val contentHtml: String,
    val imageUrl: String?,
    val isCorrect: Boolean,
)

/**
 * Accepted answer for a numerical question.
 *
 * [correctValue] is parsed from a field that is a JSON string in some records and a JSON number in
 * others; [rawValue] keeps the original text so nothing is lost if it fails to parse as a number.
 */
data class NumericalAnswer(
    val correctValue: Double?,
    val rawValue: String?,
    val lowerLimit: Double?,
    val upperLimit: Double?,
)

/**
 * A question with its subject/chapter context folded in, so the UI never has to walk back up the
 * original nesting to render a breadcrumb.
 */
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

/** Flattened question list plus the exam-level context shared by every question. */
data class QuestionBank(
    val examTitle: String?,
    val questions: List<Question>,
) {
    /**
     * Narrows the flow to the question kinds the UI can currently drive, keeping the original
     * ordering. Which kinds those are is decided by the caller, not baked in here, so enabling
     * another type is a one-line change rather than a rewrite.
     */
    fun supporting(types: Set<QuestionType>): QuestionBank =
        copy(questions = questions.filter { it.type in types })
}
