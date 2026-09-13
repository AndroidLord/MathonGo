package com.shubhamsinghbisht.quiz_answer.presentation.question

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shubhamsinghbisht.quiz_answer.data.local.AssetQuestionRepository

fun questionViewModelFactory(context: Context): ViewModelProvider.Factory {
    val appContext = context.applicationContext
    return viewModelFactory {
        initializer {
            QuestionViewModel(
                repository = AssetQuestionRepository(appContext),
                savedStateHandle = createSavedStateHandle(),
            )
        }
    }
}
