package com.shubhamsinghbisht.quiz_answer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionViewModel
import com.shubhamsinghbisht.quiz_answer.ui.theme.Quiz_AnswerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Quiz_AnswerTheme {
                val viewModel: QuestionViewModel = hiltViewModel()
                QuestionScreen(
                    viewModel = viewModel,
                    onBack = { finish() },
                )
            }
        }
    }
}
