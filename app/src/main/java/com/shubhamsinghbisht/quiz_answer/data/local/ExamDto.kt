package com.shubhamsinghbisht.quiz_answer.data.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Serialization models mirroring assets/data.json exactly.
 *
 * These stay separate from the domain models: every field is optional with a safe default so a
 * missing or null key in the source file degrades to an absent value instead of throwing.
 */
@Serializable
data class ExamDto(
    val title: String? = null,
    val icon: String? = null,
    val subjects: List<SubjectDto> = emptyList(),
)

@Serializable
data class SubjectDto(
    val title: String? = null,
    val icon: String? = null,
    val chapters: List<ChapterDto> = emptyList(),
)

@Serializable
data class ChapterDto(
    val title: String? = null,
    val chapterId: String? = null,
    val questions: List<QuestionDto> = emptyList(),
)

/** `_id` is an object wrapper in this file: `{"$oid": "64b169c4..."}`, never a bare string. */
@Serializable
data class ObjectIdDto(
    @SerialName("\$oid") val oid: String? = null,
)

@Serializable
data class QuestionDto(
    @SerialName("_id") val id: ObjectIdDto? = null,
    val type: String? = null,
    val question: QuestionContentDto? = null,
    val options: List<OptionDto> = emptyList(),
    /** Numerical answers appear as either a JSON string or a JSON number, so keep it untyped here. */
    val correctValue: JsonElement? = null,
    val numericalLowerLimit: Double? = null,
    val numericalUpperLimit: Double? = null,
    val videoSolution: VideoSolutionDto? = null,
    val isVideoSolutionAvailable: Boolean = false,
    val isRemoved: Boolean = false,
    /** Plain labels such as "JEE Advanced 2007 (Paper 2)", not objects. */
    val previousYearPapers: List<String> = emptyList(),
)

@Serializable
data class QuestionContentDto(
    val text: String? = null,
    val image: String? = null,
)

@Serializable
data class OptionDto(
    val id: String? = null,
    val text: String? = null,
    val image: String? = null,
    val isCorrect: Boolean = false,
)

@Serializable
data class VideoSolutionDto(
    val videoId: String? = null,
    val provider: String? = null,
    val thumbnailLight: String? = null,
    val thumbnailDark: String? = null,
    val description: String? = null,
)
