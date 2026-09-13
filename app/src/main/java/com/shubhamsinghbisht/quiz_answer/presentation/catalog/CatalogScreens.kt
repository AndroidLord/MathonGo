package com.shubhamsinghbisht.quiz_answer.presentation.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shubhamsinghbisht.quiz_answer.domain.model.Chapter
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.model.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScaffold(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        content = content,
    )
}

@Composable
private fun RowCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun CatalogStateHost(
    state: CatalogUiState,
    onRetry: () -> Unit,
    ready: @Composable (QuestionBank) -> Unit,
) {
    when (state) {
        CatalogUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        is CatalogUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp),
            ) {
                Text(state.message, textAlign = TextAlign.Center)
                Button(onClick = onRetry) { Text("Try again") }
            }
        }

        is CatalogUiState.Ready -> ready(state.bank)
    }
}

@Composable
fun HomeScreen(
    state: CatalogUiState,
    onRetry: () -> Unit,
    onSubjectClick: (Subject) -> Unit,
) {
    val examTitle = (state as? CatalogUiState.Ready)?.bank?.examTitle
    CatalogScaffold(title = examTitle ?: "Question bank", subtitle = null, onBack = null) { padding ->
        CatalogStateHost(state, onRetry) { bank ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(bank.subjects, key = { it.id }) { subject ->
                    RowCard(
                        title = subject.title,
                        subtitle = "${subject.chapters.size} chapters · " +
                            "${subject.questionCount} questions",
                        onClick = { onSubjectClick(subject) },
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectScreen(
    state: CatalogUiState,
    subjectId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onChapterClick: (Chapter) -> Unit,
) {
    val subject = (state as? CatalogUiState.Ready)?.bank?.subject(subjectId)
    CatalogScaffold(
        title = subject?.title ?: "Subject",
        subtitle = subject?.let { "${it.chapters.size} chapters" },
        onBack = onBack,
    ) { padding ->
        CatalogStateHost(state, onRetry) { bank ->
            val chapters = bank.subject(subjectId)?.chapters.orEmpty()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(chapters, key = { it.id }) { chapter ->
                    RowCard(
                        title = chapter.title,
                        subtitle = "${chapter.questions.size} questions",
                        onClick = { onChapterClick(chapter) },
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterScreen(
    state: CatalogUiState,
    chapterId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit,
) {
    val chapter = (state as? CatalogUiState.Ready)?.bank?.chapter(chapterId)
    CatalogScaffold(
        title = chapter?.title ?: "Chapter",
        subtitle = chapter?.let { "${it.questions.size} questions" },
        onBack = onBack,
    ) { padding ->
        CatalogStateHost(state, onRetry) { bank ->
            val resolved = bank.chapter(chapterId)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (resolved == null) {
                    Text("This chapter is no longer available.")
                    return@Column
                }
                val byType = resolved.questions.groupingBy { it.type }.eachCount()
                Text(resolved.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${resolved.questions.size} questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                byType.forEach { (type, count) ->
                    Text(
                        text = "${type.label()} · $count",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Button(
                    onClick = onStart,
                    enabled = resolved.questions.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Start practising")
                }
            }
        }
    }
}

private fun com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType.label(): String = when (this) {
    com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType.SINGLE_CORRECT -> "Single correct"
    com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType.MULTIPLE_CORRECT -> "Multiple correct"
    com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType.NUMERICAL -> "Numerical"
    com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType.UNKNOWN -> "Other"
}
