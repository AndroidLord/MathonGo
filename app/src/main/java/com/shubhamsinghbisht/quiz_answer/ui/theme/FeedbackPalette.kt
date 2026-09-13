package com.shubhamsinghbisht.quiz_answer.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class FeedbackPalette(
    val selected: Color,
    val selectedContainer: Color,
    val correct: Color,
    val correctContainer: Color,
    val incorrect: Color,
    val incorrectContainer: Color,
    val onAccent: Color,
)

val LightFeedback = FeedbackPalette(
    selected = Color(0xFF2F6BFF),
    selectedContainer = Color(0xFFEAF1FF),
    correct = Color(0xFF1E9E5A),
    correctContainer = Color(0xFFE6F7EE),
    incorrect = Color(0xFFD94141),
    incorrectContainer = Color(0xFFFDECEC),
    onAccent = Color(0xFFFFFFFF),
)

val DarkFeedback = FeedbackPalette(
    selected = Color(0xFF7BA4FF),
    selectedContainer = Color(0xFF16233D),
    correct = Color(0xFF5BD394),
    correctContainer = Color(0xFF10281C),
    incorrect = Color(0xFFFF8A8A),
    incorrectContainer = Color(0xFF2E1618),
    onAccent = Color(0xFF0E1116),
)

val LocalIsDarkTheme = compositionLocalOf { false }
