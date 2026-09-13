package com.shubhamsinghbisht.quiz_answer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shubhamsinghbisht.quiz_answer.presentation.navigation.AppNavHost
import com.shubhamsinghbisht.quiz_answer.presentation.theme.ThemeViewModel
import com.shubhamsinghbisht.quiz_answer.ui.theme.LocalIsDarkTheme
import com.shubhamsinghbisht.quiz_answer.ui.theme.Quiz_AnswerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.mode.collectAsStateWithLifecycle()
            Quiz_AnswerTheme(themeMode = themeMode) {
                val isDark = LocalIsDarkTheme.current
                AppNavHost(onToggleTheme = { themeViewModel.toggle(isDark) })
            }
        }
    }
}
