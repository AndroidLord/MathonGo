package com.shubhamsinghbisht.quiz_answer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.CatalogViewModel
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.ChapterScreen
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.HomeScreen
import com.shubhamsinghbisht.quiz_answer.presentation.catalog.SubjectScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionScreen
import com.shubhamsinghbisht.quiz_answer.presentation.question.QuestionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    onExit: () -> Unit = {},
) {
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            val catalogViewModel: CatalogViewModel = hiltViewModel()
            val state by catalogViewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onRetry = catalogViewModel::load,
                onSubjectClick = { navController.navigate(SubjectRoute(it.id)) },
            )
        }

        composable<SubjectRoute> { entry ->
            val catalogViewModel: CatalogViewModel = hiltViewModel()
            val state by catalogViewModel.uiState.collectAsStateWithLifecycle()
            SubjectScreen(
                state = state,
                subjectId = entry.toRoute<SubjectRoute>().subjectId,
                onRetry = catalogViewModel::load,
                onBack = { navController.popBackStack() },
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
                onStart = { navController.navigate(QuestionRoute(chapterId)) },
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
