package com.shubhamsinghbisht.quiz_answer.presentation.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank

@Composable
fun CatalogStateHost(
    state: CatalogUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    ready: @Composable (QuestionBank) -> Unit,
) {
    when (state) {
        CatalogUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        is CatalogUiState.Error -> Box(modifier.fillMaxSize(), Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp),
            ) {
                Text(
                    text = state.message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = onRetry) { Text("Try again") }
            }
        }

        is CatalogUiState.Ready -> ready(state.bank)
    }
}

private val AccentPalette = listOf(
    Color(0xFFF5A524),
    Color(0xFF22C55E),
    Color(0xFF3B82F6),
    Color(0xFFA855F7),
    Color(0xFFEF4444),
    Color(0xFF14B8A6),
    Color(0xFFEC4899),
)

fun accentFor(index: Int): Color = AccentPalette[index % AccentPalette.size]
