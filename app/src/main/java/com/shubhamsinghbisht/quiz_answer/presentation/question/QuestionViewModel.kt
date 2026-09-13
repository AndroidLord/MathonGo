package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import com.shubhamsinghbisht.quiz_answer.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class QuestionViewModel(
    private val repository: QuestionRepository,
    private val savedStateHandle: SavedStateHandle,
    private val supportedTypes: Set<QuestionType> = DEFAULT_SUPPORTED_TYPES,
) : ViewModel() {

    private val bank = MutableStateFlow<QuestionBank?>(null)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val currentIndex = savedStateHandle.getStateFlow(KEY_INDEX, 0)
    private val answers = MutableStateFlow(restoreAnswers())

    val uiState: StateFlow<QuestionUiState> =
        combine(bank, currentIndex, answers, errorMessage) { loaded, index, answerStates, error ->
            when {
                error != null -> QuestionUiState.Error(error)
                loaded == null -> QuestionUiState.Loading
                else -> {
                    val safeIndex = index.coerceIn(0, loaded.questions.lastIndex)
                    val question = loaded.questions[safeIndex]
                    QuestionUiState.Ready(
                        examTitle = loaded.examTitle,
                        question = question,
                        questionNumber = safeIndex + 1,
                        totalQuestions = loaded.questions.size,
                        answer = answerStates[question.id] ?: AnswerState(),
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuestionUiState.Loading)

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            errorMessage.value = null
            bank.value = null
            repository.loadQuestions()
                .map { it.supporting(supportedTypes) }
                .onSuccess { filtered ->
                    if (filtered.questions.isEmpty()) {
                        errorMessage.value = "No questions available for this exam."
                    } else {
                        bank.value = filtered
                    }
                }
                .onFailure { errorMessage.value = "Couldn't load the questions. Please try again." }
        }
    }

    fun onOptionClicked(optionId: String) {
        val ready = uiState.value as? QuestionUiState.Ready ?: return
        if (ready.answer.checked) return
        if (ready.question.options.none { it.id == optionId }) return

        updateAnswer(ready.question.id) { current ->
            val selection = when (ready.question.type) {
                QuestionType.MULTIPLE_CORRECT ->
                    if (optionId in current.selectedOptionIds) current.selectedOptionIds - optionId
                    else current.selectedOptionIds + optionId
                else -> setOf(optionId)
            }
            current.copy(selectedOptionIds = selection)
        }
    }

    fun onNumericalInputChanged(input: String) {
        val ready = uiState.value as? QuestionUiState.Ready ?: return
        if (ready.answer.checked) return
        updateAnswer(ready.question.id) { it.copy(numericalInput = input) }
    }

    fun onCheckAnswer() {
        val ready = uiState.value as? QuestionUiState.Ready ?: return
        if (!ready.canCheck) return
        updateAnswer(ready.question.id) { it.copy(checked = true) }
    }

    fun onNext() = moveTo(currentIndex.value + 1)

    fun onPrevious() = moveTo(currentIndex.value - 1)

    private fun moveTo(target: Int) {
        val questions = bank.value?.questions ?: return
        if (target !in questions.indices) return
        savedStateHandle[KEY_INDEX] = target
    }

    private fun updateAnswer(questionId: String, transform: (AnswerState) -> AnswerState) {
        answers.update { current ->
            val next = current + (questionId to transform(current[questionId] ?: AnswerState()))
            savedStateHandle[KEY_ANSWERS] = json.encodeToString(next)
            next
        }
    }

    private fun restoreAnswers(): Map<String, AnswerState> {
        val stored = savedStateHandle.get<String>(KEY_ANSWERS) ?: return emptyMap()
        return runCatching { json.decodeFromString<Map<String, AnswerState>>(stored) }
            .getOrDefault(emptyMap())
    }

    companion object {
        val DEFAULT_SUPPORTED_TYPES = setOf(QuestionType.SINGLE_CORRECT)

        private const val KEY_INDEX = "currentQuestionIndex"
        private const val KEY_ANSWERS = "answerStates"
        private val json = Json { ignoreUnknownKeys = true }
    }
}
