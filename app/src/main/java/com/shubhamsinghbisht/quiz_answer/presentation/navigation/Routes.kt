package com.shubhamsinghbisht.quiz_answer.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class SubjectRoute(val subjectId: String)

@Serializable
data class ChapterRoute(val chapterId: String)

@Serializable
data class QuestionRoute(val chapterId: String)
