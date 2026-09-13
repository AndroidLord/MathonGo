package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomActions(
    modifier: Modifier = Modifier,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    canCheck: Boolean,
    onPrevious: () -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = canGoPrevious,
                modifier = Modifier.weight(1f),
            ) {
                Text("Previous")
            }
            Button(
                onClick = onCheck,
                enabled = canCheck,
                modifier = Modifier.weight(1.4f),
            ) {
                Text("Check Answer")
            }
            OutlinedButton(
                onClick = onNext,
                enabled = canGoNext,
                modifier = Modifier.weight(1f),
            ) {
                Text("Next")
            }
        }
    }
}
