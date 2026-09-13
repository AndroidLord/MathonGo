package com.shubhamsinghbisht.quiz_answer.data.mapper

import com.shubhamsinghbisht.quiz_answer.data.local.ExamDto
import com.shubhamsinghbisht.quiz_answer.data.local.QuestionDto
import com.shubhamsinghbisht.quiz_answer.domain.model.Chapter
import com.shubhamsinghbisht.quiz_answer.domain.model.NumericalAnswer
import com.shubhamsinghbisht.quiz_answer.domain.model.Option
import com.shubhamsinghbisht.quiz_answer.domain.model.Question
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import com.shubhamsinghbisht.quiz_answer.domain.model.Subject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

fun ExamDto.toQuestionBank(): QuestionBank {
    var position = 0
    val mappedSubjects = subjects.mapIndexed { subjectIndex, subjectDto ->
        val subjectId = subjectDto.id?.oid?.takeIf { it.isNotBlank() } ?: "subject-$subjectIndex"
        val mappedChapters = subjectDto.chapters.mapIndexed { chapterIndex, chapterDto ->
            val chapterId = chapterDto.id?.oid?.takeIf { it.isNotBlank() }
                ?: "$subjectId-chapter-$chapterIndex"
            val chapterQuestions = chapterDto.questions.mapNotNull { dto ->
                if (dto.isRemoved) return@mapNotNull null
                dto.toQuestion(
                    examTitle = title,
                    subjectId = subjectId,
                    subjectTitle = subjectDto.title,
                    chapterId = chapterId,
                    chapterTitle = chapterDto.title ?: chapterDto.chapterId,
                    fallbackId = "q-${position++}",
                )
            }
            Chapter(
                id = chapterId,
                title = chapterDto.title ?: chapterDto.chapterId.orEmpty(),
                subjectId = subjectId,
                questions = chapterQuestions,
            )
        }
        Subject(
            id = subjectId,
            title = subjectDto.title.orEmpty(),
            chapters = mappedChapters,
        )
    }

    return QuestionBank(
        examTitle = title,
        subjects = mappedSubjects,
        questions = mappedSubjects.flatMap { subject -> subject.chapters.flatMap { it.questions } },
    )
}

private fun QuestionDto.toQuestion(
    examTitle: String?,
    subjectId: String,
    subjectTitle: String?,
    chapterId: String,
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
        subjectId = subjectId,
        chapterId = chapterId,
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

private fun QuestionDto.toNumericalAnswer() = NumericalAnswer(
    correctValue = correctValue.asDoubleOrNull(),
    rawValue = correctValue.asContentOrNull(),
    lowerLimit = numericalLowerLimit,
    upperLimit = numericalUpperLimit,
)

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
