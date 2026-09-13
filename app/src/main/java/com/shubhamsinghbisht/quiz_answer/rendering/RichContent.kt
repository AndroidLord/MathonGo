package com.shubhamsinghbisht.quiz_answer.rendering

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// Placeholder renderer for the screen skeleton. Phase 8 replaces the body with the WebView
// based MathContentWebView so HTML, LaTeX and MathML render properly.
@Composable
fun RichContent(
    html: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = LocalContentColor.current,
) {
    Text(
        text = html.asPlainTextPreview(),
        style = textStyle,
        color = color,
        modifier = modifier,
    )
}

private fun String.asPlainTextPreview(): String =
    replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]+>"), " ")
        .replace("&nbsp;", " ")
        .replace("&#160;", " ")
        .replace(Regex("[ \\t]{2,}"), " ")
        .trim()
