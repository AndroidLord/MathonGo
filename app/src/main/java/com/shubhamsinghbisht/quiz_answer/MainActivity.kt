package com.shubhamsinghbisht.quiz_answer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shubhamsinghbisht.quiz_answer.presentation.navigation.AppNavHost
import com.shubhamsinghbisht.quiz_answer.ui.theme.Quiz_AnswerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Quiz_AnswerTheme {
                AppNavHost(onExit = { finish() })
            }
        }
    }
}
