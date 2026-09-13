package com.shubhamsinghbisht.quiz_answer.rendering

import android.content.Context
import android.webkit.WebView

private val BLOCK_BREAK = Regex("</(p|div|tr|table|li)>|<br\\s*/?>", RegexOption.IGNORE_CASE)
private val TAG = Regex("<[^>]+>")
private val NUMERIC_ENTITY = Regex("&#(\\d+);")
private val HEX_ENTITY = Regex("&#x([0-9a-fA-F]+);", RegexOption.IGNORE_CASE)

private val NAMED_ENTITIES = mapOf(
    "&nbsp;" to " ", "&amp;" to "&", "&lt;" to "<", "&gt;" to ">",
    "&quot;" to "\"", "&apos;" to "'", "&times;" to "×", "&divide;" to "÷",
    "&deg;" to "°", "&plusmn;" to "±", "&hellip;" to "…", "&rarr;" to "→",
)

// Last-resort plain text for when the WebView cannot render. Keeps the wording and the symbols
// rather than dropping the question entirely.
fun String.toPlainText(): String {
    var text = BLOCK_BREAK.replace(this, "\n")
    text = TAG.replace(text, " ")
    NAMED_ENTITIES.forEach { (entity, replacement) -> text = text.replace(entity, replacement) }
    text = NUMERIC_ENTITY.replace(text) { match ->
        match.groupValues[1].toIntOrNull()?.let { String(Character.toChars(it)) } ?: match.value
    }
    text = HEX_ENTITY.replace(text) { match ->
        match.groupValues[1].toIntOrNull(16)?.let { String(Character.toChars(it)) } ?: match.value
    }
    return text
        .replace(Regex("[ \\t]{2,}"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}

fun isWebViewUsable(context: Context): Boolean = runCatching {
    WebView(context).destroy()
    true
}.getOrDefault(false)
