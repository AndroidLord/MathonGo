package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shubhamsinghbisht.quiz_answer.rendering.RichContent
import com.shubhamsinghbisht.quiz_answer.ui.theme.optionColors

@Composable
fun OptionCard(
    label: String,
    contentHtml: String,
    optionId: String,
    answerState: QuestionAnswerState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = optionColors(answerState, optionId)
    val border by animateColorAsState(colors.border, label = "optionBorder")
    val container by animateColorAsState(colors.container, label = "optionContainer")

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 9.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = container,
                disabledContainerColor = container,
                contentColor = colors.content,
                disabledContentColor = colors.content,
            ),
            border = BorderStroke(if (colors.emphasized) 1.5.dp else 1.dp, border),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OptionBadge(label = label, colors = colors)
                RichContent(
                    html = contentHtml,
                    color = colors.content,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    interactive = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        val stateLabel = colors.label
        if (stateLabel != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 14.dp)
                    .background(container)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.labelColor,
                )
                colors.labelIcon?.let { icon ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colors.labelColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colors.badgeContent,
                            modifier = Modifier.size(11.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionBadge(label: String, colors: com.shubhamsinghbisht.quiz_answer.ui.theme.OptionColors) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(colors.badgeContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (colors.emphasized) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.badgeContent,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
