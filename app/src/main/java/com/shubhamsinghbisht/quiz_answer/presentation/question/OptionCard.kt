package com.shubhamsinghbisht.quiz_answer.presentation.question

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.shubhamsinghbisht.quiz_answer.rendering.RichContent
import com.shubhamsinghbisht.quiz_answer.ui.theme.optionColors

@Composable
fun OptionCard(
    label: String,
    contentHtml: String,
    visualState: OptionVisualState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = optionColors(visualState)
    val border by animateColorAsState(colors.border, label = "optionBorder")
    val container by animateColorAsState(colors.container, label = "optionContainer")

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = container,
            disabledContainerColor = container,
            contentColor = colors.content,
            disabledContentColor = colors.content,
        ),
        border = BorderStroke(if (visualState == OptionVisualState.NEUTRAL) 1.dp else 2.dp, border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = colors.badgeContainer,
                contentColor = colors.badgeContent,
                modifier = Modifier.size(28.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when (visualState) {
                        OptionVisualState.CORRECT -> Icon(
                            Icons.Default.Check,
                            contentDescription = "Correct",
                            modifier = Modifier.size(18.dp),
                        )
                        OptionVisualState.INCORRECT -> Icon(
                            Icons.Default.Close,
                            contentDescription = "Incorrect",
                            modifier = Modifier.size(18.dp),
                        )
                        else -> Text(label, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            RichContent(
                html = contentHtml,
                color = colors.content,
                textStyle = MaterialTheme.typography.bodyMedium,
                interactive = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
