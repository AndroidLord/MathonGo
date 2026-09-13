package com.shubhamsinghbisht.quiz_answer.rendering

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import kotlin.math.max

private const val ASSET_DOMAIN = "appassets.androidplatform.net"
private const val BASE_URL = "https://$ASSET_DOMAIN/assets/"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathContentWebView(
    html: String,
    theme: RichContentTheme,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    onRenderFailed: () -> Unit = {},
) {
    val context = LocalContext.current
    val template = remember(context) { RichContentTemplate(context) }
    val document = remember(html, theme) { template.build(html, theme) }

    // Not keyed on the document: keeping the previous height avoids a collapse-and-reflow
    // flash while the next question typesets.
    var heightDp by remember { mutableFloatStateOf(0f) }
    val loadedDocument = remember { arrayOfNulls<String>(1) }

    val assetLoader = remember(context) {
        WebViewAssetLoader.Builder()
            .setDomain(ASSET_DOMAIN)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }
    val imageLoader = remember(context) { WebImageLoader(context) }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(max(heightDp, 1f).dp),
        factory = { ctx ->
            RichWebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                setBackgroundColor(AndroidColor.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = WebView.OVER_SCROLL_NEVER
                isFocusable = false
                isFocusableInTouchMode = false

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                settings.loadsImagesAutomatically = true
                settings.blockNetworkImage = false
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false

                // The viewport is pinned to initial-scale=1, so one CSS pixel is one dp and the
                // reported height needs no density conversion.
                addJavascriptInterface(
                    HeightBridge { reported -> post { heightDp = reported.toFloat() } },
                    "AndroidBridge",
                )

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest,
                    ): WebResourceResponse? =
                        assetLoader.shouldInterceptRequest(request.url)
                            ?: imageLoader.intercept(request.url)

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean = true

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: android.webkit.WebResourceError,
                    ) {
                        // A failed subresource must not take the screen down with it.
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        // Backstop in case MathJax never reaches its ready callback.
                        view.evaluateJavascript("postHeight && postHeight()", null)
                    }

                    // Returning false here lets the render process crash kill the whole app.
                    override fun onRenderProcessGone(
                        view: WebView,
                        detail: RenderProcessGoneDetail,
                    ): Boolean {
                        view.destroy()
                        onRenderFailed()
                        return true
                    }
                }
            }
        },
        update = { webView ->
            webView.touchEnabled = interactive
            if (loadedDocument[0] != document) {
                loadedDocument[0] = document
                webView.loadDataWithBaseURL(BASE_URL, document, "text/html", "utf-8", null)
            }
        },
        onRelease = { it.destroy() },
    )
}

// An embedded WebView consumes touches by default, which would stop an option card from ever
// seeing a tap. Options opt out of touch so the event reaches the Compose parent.
private class RichWebView(context: Context) : WebView(context) {
    var touchEnabled: Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (touchEnabled) super.onTouchEvent(event) else false
}

private class HeightBridge(private val onHeight: (Int) -> Unit) {

    @JavascriptInterface
    fun onHeightChanged(heightPx: Int) {
        if (heightPx > 0) onHeight(heightPx)
    }

    @JavascriptInterface
    fun onRenderComplete() = Unit
}
