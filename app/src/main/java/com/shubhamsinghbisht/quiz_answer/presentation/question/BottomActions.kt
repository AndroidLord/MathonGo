package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val PillShape = RoundedCornerShape(40.dp)
private val ButtonHeight = 48.dp
private val NavButtonWidth = 94.dp
private val ButtonPadding = 12.dp
private val EdgePadding = 8.dp
private val BottomPadding = 32.dp
private val ButtonGap = 12.dp

@Composable
fun BottomActions(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    canCheck: Boolean,
    onPrevious: () -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The spec's 32dp bottom already covers a gesture bar; take the larger value so a
    // three-button navigation bar cannot sit on top of the actions.
    val systemBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(
                start = EdgePadding,
                top = EdgePadding,
                end = EdgePadding,
                bottom = maxOf(BottomPadding, systemBottom),
            ),
            horizontalArrangement = Arrangement.spacedBy(ButtonGap),
        ) {
            NavPill(text = "Previous", enabled = canGoPrevious, onClick = onPrevious)

            Button(
                onClick = onCheck,
                enabled = canCheck,
                shape = PillShape,
                contentPadding = PaddingValues(horizontal = ButtonPadding),
                modifier = Modifier
                    .weight(1f)
                    .height(ButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = "Check Answer",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            NavPill(text = "Next", enabled = canGoNext, onClick = onNext)
        }
    }
}

@Composable
private fun NavPill(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val stroke = MaterialTheme.colorScheme.outlineVariant
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        contentPadding = PaddingValues(horizontal = ButtonPadding),
        border = BorderStroke(1.dp, if (enabled) stroke else stroke.copy(alpha = 0.5f)),
        modifier = Modifier
            .width(NavButtonWidth)
            .height(ButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
