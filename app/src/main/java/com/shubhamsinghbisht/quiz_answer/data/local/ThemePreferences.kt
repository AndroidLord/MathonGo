package com.shubhamsinghbisht.quiz_answer.data.local

import android.content.Context
import com.shubhamsinghbisht.quiz_answer.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class ThemePreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val prefs by lazy { context.getSharedPreferences(FILE, Context.MODE_PRIVATE) }

    private val _mode = MutableStateFlow(read())
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    fun set(mode: ThemeMode) {
        _mode.value = mode
        prefs.edit().putString(KEY, mode.name).apply()
    }

    private fun read(): ThemeMode = runCatching {
        ThemeMode.valueOf(prefs.getString(KEY, null) ?: ThemeMode.DARK.name)
    }.getOrDefault(ThemeMode.DARK)

    private companion object {
        const val FILE = "appearance"
        const val KEY = "themeMode"
    }
}
