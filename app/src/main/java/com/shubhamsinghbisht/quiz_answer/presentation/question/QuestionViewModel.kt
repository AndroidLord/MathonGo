package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shubhamsinghbisht.quiz_answer.presentation.navigation.QuestionRoute
import com.shubhamsinghbisht.quiz_answer.core.util.AnswerChecker
import com.shubhamsinghbisht.quiz_answer.di.QuestionFlowConfig
import com.shubhamsinghbisht.quiz_answer.domain.model.Question
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.serialization.json.Json

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val savedStateHandle: SavedStateHandle,
    private val flowConfig: QuestionFlowConfig,
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
                    val record = answerStates[question.id] ?: AnswerRecord()
                    QuestionUiState.Ready(
                        examTitle = loaded.examTitle,
                        question = question,
                        questionNumber = safeIndex + 1,
                        totalQuestions = loaded.questions.size,
                        record = record,
                        answerState = answerStateOf(question, record),
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
            val chapterId = savedStateHandle.toRoute<QuestionRoute>().chapterId
            repository.loadQuestions()
                .map { it.inChapter(chapterId).supporting(flowConfig.supportedTypes) }
                .onSuccess { filtered ->
                    if (filtered.questions.isEmpty()) {
                        errorMessage.value = "No questions available in this chapter."
                    } else {
                        bank.value = filtered
                    }
                }
                .onFailure { errorMessage.value = "Couldn't load the questions. Please try again." }
        }
    }

    fun onOptionClicked(optionId: String) {
        val ready = uiState.value as? QuestionUiState.Ready ?: return
        if (ready.isChecked) return
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
        if (ready.isChecked) return
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

    private fun answerStateOf(
        question: Question,
        record: AnswerRecord,
    ): QuestionAnswerState = when {
        !record.checked && !record.hasResponse -> QuestionAnswerState.Unanswered
        !record.checked -> QuestionAnswerState.Selected(record.selectedOptionIds)
        question.type == QuestionType.NUMERICAL ->
            if (AnswerChecker.isNumericalCorrect(question, record.numericalInput)) {
                QuestionAnswerState.CheckedCorrect(record.selectedOptionIds)
            } else {
                QuestionAnswerState.CheckedIncorrect(emptySet(), emptySet())
            }
        AnswerChecker.isCorrect(question, record.selectedOptionIds) ->
            QuestionAnswerState.CheckedCorrect(record.selectedOptionIds)
        else -> QuestionAnswerState.CheckedIncorrect(
            selectedOptionIds = record.selectedOptionIds,
            correctOptionIds = question.correctOptionIds,
        )
    }

    private fun updateAnswer(questionId: String, transform: (AnswerRecord) -> AnswerRecord) {
        answers.update { current ->
            val next = current + (questionId to transform(current[questionId] ?: AnswerRecord()))
            savedStateHandle[KEY_ANSWERS] = json.encodeToString(next)
            next
        }
    }

    private fun restoreAnswers(): Map<String, AnswerRecord> {
        val stored = savedStateHandle.get<String>(KEY_ANSWERS) ?: return emptyMap()
        return runCatching { json.decodeFromString<Map<String, AnswerRecord>>(stored) }
            .getOrDefault(emptyMap())
    }

    companion object {
        private const val KEY_INDEX = "currentQuestionIndex"
        private const val KEY_ANSWERS = "answerStates"
        private val json = Json { ignoreUnknownKeys = true }
    }
}
