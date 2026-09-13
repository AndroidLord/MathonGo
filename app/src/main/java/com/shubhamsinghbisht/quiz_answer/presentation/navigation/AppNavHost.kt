package com.shubhamsinghbisht.quiz_answer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.CatalogViewModel
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.ChapterScreen
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.HomeScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    onToggleTheme: () -> Unit = {},
) {
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            val catalogViewModel: CatalogViewModel = hiltViewModel()
            val state by catalogViewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onRetry = catalogViewModel::load,
                onToggleTheme = onToggleTheme,
                onChapterClick = { navController.navigate(ChapterRoute(it.id)) },
            )
        }

        composable<ChapterRoute> { entry ->
            val chapterId = entry.toRoute<ChapterRoute>().chapterId
            val catalogViewModel: CatalogViewModel = hiltViewModel()
            val state by catalogViewModel.uiState.collectAsStateWithLifecycle()
            ChapterScreen(
                state = state,
                chapterId = chapterId,
                onRetry = catalogViewModel::load,
                onBack = { navController.popBackStack() },
                onQuestionClick = { index ->
                    navController.navigate(QuestionRoute(chapterId, index))
                },
            )
        }

        composable<QuestionRoute> {
            val questionViewModel: QuestionViewModel = hiltViewModel()
            QuestionScreen(
                viewModel = questionViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
