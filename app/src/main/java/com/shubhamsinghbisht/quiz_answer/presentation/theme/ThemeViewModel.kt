package com.shubhamsinghbisht.quiz_answer.presentation.theme

import androidx.lifecycle.ViewModel
import com.shubhamsinghbisht.quiz_answer.data.local.ThemePreferences
import com.shubhamsinghbisht.quiz_answer.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferences: ThemePreferences,
) : ViewModel() {

    val mode: StateFlow<ThemeMode> = preferences.mode

    fun toggle(isCurrentlyDark: Boolean) {
        preferences.set(if (isCurrentlyDark) ThemeMode.LIGHT else ThemeMode.DARK)
    }
}
