package com.shubhamsinghbisht.quiz_answer.rendering

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class WebImageLoader(context: Context) {

    private val cacheDir = File(context.cacheDir, "web-images").apply { mkdirs() }
    private val inFlight = ConcurrentHashMap<String, Any>()

    fun intercept(url: Uri): WebResourceResponse? {
        val scheme = url.scheme?.lowercase() ?: return null
        if (scheme != "http" && scheme != "https") return null
        val raw = url.toString()
        if (!looksLikeImage(url)) return null

        cached(raw)?.let { return it }

        // One download per URL: a question can repeat the same image across its options.
        val lock = inFlight.getOrPut(raw) { Any() }
        synchronized(lock) {
            cached(raw)?.let { return it }
            return download(raw)
        }
    }

    private fun cached(url: String): WebResourceResponse? {
        val file = fileFor(url)
        if (!file.exists() || file.length() == 0L) return null
        return runCatching {
            WebResourceResponse(mimeTypeOf(url), null, FileInputStream(file))
        }.getOrNull()
    }

    private fun download(url: String): WebResourceResponse? = runCatching {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            requestMethod = "GET"
        }
        try {
            if (connection.responseCode !in 200..299) return null
            val bytes = connection.inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) return null

            val target = fileFor(url)
            val temp = File(target.parentFile, "${target.name}.part")
            temp.writeBytes(bytes)
            if (!temp.renameTo(target)) temp.delete()

            val mime = connection.contentType?.substringBefore(';')?.trim()?.takeIf {
                it.startsWith("image/")
            } ?: mimeTypeOf(url)
            WebResourceResponse(mime, null, bytes.inputStream())
        } finally {
            connection.disconnect()
        }
    }.getOrNull()

    private fun fileFor(url: String): File = File(cacheDir, "${url.hashCode()}")

    private fun looksLikeImage(url: Uri): Boolean {
        val path = url.path?.lowercase().orEmpty()
        return IMAGE_SUFFIXES.any { path.endsWith(it) } || path.contains(".png") ||
            path.contains(".jpg") || path.contains(".jpeg")
    }

    private fun mimeTypeOf(url: String): String {
        val path = url.substringBefore('?').lowercase()
        return when {
            path.endsWith(".png") -> "image/png"
            path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
            path.endsWith(".gif") -> "image/gif"
            path.endsWith(".webp") -> "image/webp"
            path.endsWith(".svg") -> "image/svg+xml"
            else -> "image/*"
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 15_000
        val IMAGE_SUFFIXES = listOf(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg", ".bmp")
    }
}
