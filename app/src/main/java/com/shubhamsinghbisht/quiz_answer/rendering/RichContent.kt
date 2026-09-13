package com.shubhamsinghbisht.quiz_answer.rendering

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isSpecified

@Composable
fun RichContent(
    html: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = LocalContentColor.current,
    interactive: Boolean = true,
) {
    val context = LocalContext.current
    val webViewUsable = remember(context) { isWebViewUsable(context) }
    var renderFailed by remember(html) { mutableStateOf(false) }

    if (!webViewUsable || renderFailed) {
        Text(
            text = html.toPlainText(),
            style = textStyle,
            color = color,
            modifier = modifier,
        )
        return
    }

    val scheme = MaterialTheme.colorScheme
    val fontSizeSp = if (textStyle.fontSize.isSpecified) textStyle.fontSize.value else 16f
    val lineHeight = if (textStyle.lineHeight.isSpecified) {
        (textStyle.lineHeight.value / fontSizeSp).coerceIn(1.1f, 2f)
    } else {
        1.45f
    }

    val theme = remember(color, scheme, fontSizeSp, lineHeight) {
        RichContentTheme(
            textColor = color,
            linkColor = scheme.primary,
            borderColor = scheme.outlineVariant,
            fontSizeSp = fontSizeSp,
            lineHeight = lineHeight,
        )
    }

    MathContentWebView(
        html = html,
        theme = theme,
        modifier = modifier,
        interactive = interactive,
        onRenderFailed = { renderFailed = true },
    )
}
