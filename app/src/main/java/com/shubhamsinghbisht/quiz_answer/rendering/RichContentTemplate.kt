package com.shubhamsinghbisht.quiz_answer.rendering

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class RichContentTheme(
    val textColor: Color,
    val linkColor: Color,
    val borderColor: Color,
    val fontSizeSp: Float,
    val lineHeight: Float,
)

class RichContentTemplate(context: Context) {

    private val template: String = runCatching {
        context.assets.open(TEMPLATE_ASSET).bufferedReader().use { it.readText() }
    }.getOrElse { FALLBACK_TEMPLATE }

    fun build(content: String, theme: RichContentTheme): String = template
        .replace("__TEXT_COLOR__", theme.textColor.toCss())
        .replace("__LINK_COLOR__", theme.linkColor.toCss())
        .replace("__BORDER_COLOR__", theme.borderColor.toCss())
        .replace("__FONT_SIZE__", theme.fontSizeSp.toString())
        .replace("__LINE_HEIGHT__", theme.lineHeight.toString())
        .replace("__CONTENT__", content)

    private companion object {
        const val TEMPLATE_ASSET = "rich_content.html"
        const val FALLBACK_TEMPLATE =
            "<!DOCTYPE html><html><body><div id=\"root\">__CONTENT__</div></body></html>"
    }
}

private fun Color.toCss(): String {
    val argb = toArgb()
    return "rgba(${(argb shr 16) and 0xFF}, ${(argb shr 8) and 0xFF}, ${argb and 0xFF}, " +
        "${((argb shr 24) and 0xFF) / 255f})"
}
