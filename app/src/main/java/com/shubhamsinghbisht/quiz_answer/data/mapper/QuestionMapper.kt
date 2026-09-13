package com.shubhamsinghbisht.quiz_answer.data.mapper

import com.shubhamsinghbisht.quiz_answer.data.local.ExamDto
import com.shubhamsinghbisht.quiz_answer.data.local.QuestionDto
import com.shubhamsinghbisht.quiz_answer.domain.model.NumericalAnswer
import com.shubhamsinghbisht.quiz_answer.domain.model.Option
import com.shubhamsinghbisht.quiz_answer.domain.model.Question
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.JsonElement

/**
 * Flattens exam -> subject -> chapter -> question into a single list, preserving the original
 * ordering of the JSON arrays, and copies the subject/chapter context down onto each question.
 */
fun ExamDto.toQuestionBank(): QuestionBank {
    val flattened = buildList {
        subjects.forEach { subject ->
            subject.chapters.forEach { chapter ->
                chapter.questions.forEach { dto ->
                    if (dto.isRemoved) return@forEach
                    val question = dto.toQuestion(
                        examTitle = title,
                        subjectTitle = subject.title,
                        chapterTitle = chapter.title ?: chapter.chapterId,
                        fallbackId = "q-${size}",
                    )
                    if (question != null) add(question)
                }
            }
        }
    }
    return QuestionBank(examTitle = title, questions = flattened)
}

/**
 * Returns null only when the record carries no renderable content at all. [fallbackId] covers the
 * case of a missing `_id`; it is derived from position in the flattened list, never used as the
 * answer-state key when a real id exists.
 */
private fun QuestionDto.toQuestion(
    examTitle: String?,
    subjectTitle: String?,
    chapterTitle: String?,
    fallbackId: String,
): Question? {
    val contentHtml = question?.text.orEmpty()
    val mappedOptions = options.mapIndexedNotNull { index, option ->
        val text = option.text.orEmpty()
        val image = option.image?.takeIf { it.isNotBlank() }
        if (text.isBlank() && image == null) return@mapIndexedNotNull null
        Option(
            id = option.id?.takeIf { it.isNotBlank() } ?: "$fallbackId-opt-$index",
            contentHtml = text,
            imageUrl = image,
            isCorrect = option.isCorrect,
        )
    }
    val imageUrl = question?.image?.takeIf { it.isNotBlank() }
    if (contentHtml.isBlank() && imageUrl == null && mappedOptions.isEmpty()) return null

    val type = QuestionType.fromRaw(type)
    return Question(
        id = id?.oid?.takeIf { it.isNotBlank() } ?: fallbackId,
        type = type,
        contentHtml = contentHtml,
        imageUrl = imageUrl,
        options = mappedOptions,
        examTitle = examTitle?.takeIf { it.isNotBlank() },
        subjectTitle = subjectTitle?.takeIf { it.isNotBlank() },
        chapterTitle = chapterTitle?.takeIf { it.isNotBlank() },
        previousYearPapers = previousYearPapers.filter { it.isNotBlank() },
        hasVideoSolution = isVideoSolutionAvailable || videoSolution != null,
        numericalAnswer = if (type == QuestionType.NUMERICAL) toNumericalAnswer() else null,
    )
}

private fun QuestionDto.toNumericalAnswer(): NumericalAnswer {
    val raw = correctValue.asContentOrNull()
    return NumericalAnswer(
        correctValue = correctValue.asDoubleOrNull(),
        rawValue = raw,
        lowerLimit = numericalLowerLimit,
        upperLimit = numericalUpperLimit,
    )
}

/** `correctValue` is a JSON string in most records and a JSON number in the rest. */
private fun JsonElement?.asContentOrNull(): String? {
    val primitive = this as? JsonPrimitive ?: return null
    if (primitive is JsonNull) return null
    return primitive.content.takeIf { it.isNotBlank() }
}

private fun JsonElement?.asDoubleOrNull(): Double? {
    val primitive = this as? JsonPrimitive ?: return null
    if (primitive is JsonNull) return null
    return primitive.doubleOrNull ?: primitive.content.trim().toDoubleOrNull()
}
