package com.shubhamsinghbisht.quiz_answer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionViewModel
import com.shubhamsinghbisht.quiz_answer.presentation.question.questionViewModelFactory
import com.shubhamsinghbisht.quiz_answer.ui.theme.Quiz_AnswerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Quiz_AnswerTheme {
                val viewModel: QuestionViewModel = viewModel(
                    factory = questionViewModelFactory(applicationContext),
                )
                QuestionScreen(
                    viewModel = viewModel,
                    onBack = { finish() },
                )
            }
        }
    }
}
