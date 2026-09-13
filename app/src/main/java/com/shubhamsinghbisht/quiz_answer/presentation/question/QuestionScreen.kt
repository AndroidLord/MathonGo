package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType

@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    QuestionScreen(
        state = state,
        onBack = onBack,
        onOptionClick = viewModel::onOptionClicked,
        onNumericalInputChange = viewModel::onNumericalInputChanged,
        onCheck = viewModel::onCheckAnswer,
        onPrevious = viewModel::onPrevious,
        onNext = viewModel::onNext,
        onRetry = viewModel::load,
        modifier = modifier,
    )
}

@Composable
fun QuestionScreen(
    state: QuestionUiState,
    onBack: () -> Unit,
    onOptionClick: (String) -> Unit,
    onNumericalInputChange: (String) -> Unit,
    onCheck: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ready = state as? QuestionUiState.Ready

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            QuestionHeader(
                examTitle = ready?.examTitle,
                chapterTitle = ready?.question?.chapterTitle,
                onBack = onBack,
            )
        },
        bottomBar = {
            if (ready != null) {
                BottomActions(
                    canGoPrevious = !ready.isFirst,
                    canGoNext = !ready.isLast,
                    canCheck = ready.canCheck,
                    onPrevious = onPrevious,
                    onCheck = onCheck,
                    onNext = onNext,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state) {
                QuestionUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                is QuestionUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )

                is QuestionUiState.Ready -> QuestionBody(
                    state = state,
                    onOptionClick = onOptionClick,
                    onNumericalInputChange = onNumericalInputChange,
                )
            }
        }
    }
}

@Composable
private fun QuestionBody(
    state: QuestionUiState.Ready,
    onOptionClick: (String) -> Unit,
    onNumericalInputChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuestionContent(
            question = state.question,
            questionNumber = state.questionNumber,
            totalQuestions = state.totalQuestions,
        )

        if (state.question.type == QuestionType.NUMERICAL) {
            NumericalAnswerInput(
                value = state.record.numericalInput,
                answerState = state.answerState,
                expectedAnswer = state.question.numericalAnswer?.rawValue,
                enabled = !state.isChecked,
                onValueChange = onNumericalInputChange,
            )
        } else if (state.question.options.isEmpty()) {
            Text(
                text = "This question has no options to choose from.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.question.options.forEachIndexed { index, option ->
                    OptionCard(
                        label = ('A' + index).toString(),
                        contentHtml = option.contentHtml,
                        optionId = option.id,
                        answerState = state.answerState,
                        enabled = !state.isChecked,
                        onClick = { onOptionClick(option.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) { Text("Try again") }
    }
}
