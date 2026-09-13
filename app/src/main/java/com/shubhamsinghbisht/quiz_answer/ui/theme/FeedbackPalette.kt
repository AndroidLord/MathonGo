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
    selected = Color(0xFF2563EB),
    selectedContainer = Color(0xFFEFF5FF),
    correct = Color(0xFF16A34A),
    correctContainer = Color(0xFFEDFBF2),
    incorrect = Color(0xFFDC2626),
    incorrectContainer = Color(0xFFFEF2F2),
    onAccent = Color(0xFFFFFFFF),
)

val DarkFeedback = FeedbackPalette(
    selected = Color(0xFF3B82F6),
    selectedContainer = Color(0xFF15203A),
    correct = Color(0xFF22C55E),
    correctContainer = Color(0xFF10231A),
    incorrect = Color(0xFFEF4444),
    incorrectContainer = Color(0xFF2A1416),
    onAccent = Color(0xFFFFFFFF),
)

val LocalIsDarkTheme = compositionLocalOf { false }
