package com.shubham.androidcustomtabs

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class WebViewState(val webView: WebView) {
    var bundle: Bundle? = null
}

private class AndroidBridge(
    private val onMessage: (String) -> Unit,
    private val onClose: () -> Unit
) {
    @JavascriptInterface
    fun postMessage(message: String) {
        onMessage(message)
    }

    @JavascriptInterface
    fun closeWebView() {
        onClose()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    url: String,
    onBack: () -> Unit,
    onSessionUpdate: (DemoSession) -> Unit
) {
    var pageTitle by remember { mutableStateOf("Loading...") }
    var currentUrl by rememberSaveable { mutableStateOf(url) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var domReadyMs by remember { mutableStateOf<Long?>(null) }
    var fullLoadMs by remember { mutableStateOf<Long?>(null) }
    var firstMeaningfulPaintMs by remember { mutableStateOf<Long?>(null) }
    var statusMessage by remember { mutableStateOf("Booting WebView engine…") }
    var lastLoadedUrl by remember { mutableStateOf("") }
    var loadStartedAtMs by remember { mutableStateOf(0L) }

    val webViewState = rememberWebViewState()
    val webView = webViewState.webView
    val context = LocalContext.current

    fun showMetricsToast(prefix: String) {
        val domPart = domReadyMs?.let { "DOM ${it}ms" } ?: "DOM --"
        val paintPart = firstMeaningfulPaintMs?.let { "paint ${it}ms" } ?: "paint --"
        val fullPart = fullLoadMs?.let { "full ${it}ms" } ?: "full --"
        Toast.makeText(
            context,
            "$prefix\n$domPart · $paintPart · $fullPart\nWebView pays setup/render cost inside your app.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun handleBridgeMessage(message: String) {
        val event = Regex("\"event\":\"([^\"]+)\"").find(message)?.groupValues?.get(1)
        val email = Regex("\"email\":\"([^\"]+)\"").find(message)?.groupValues?.get(1)
        val name = Regex("\"name\":\"([^\"]+)\"").find(message)?.groupValues?.get(1)
            ?: email?.substringBefore('@')
        val source = Regex("\"source\":\"([^\"]+)\"").find(message)?.groupValues?.get(1)
            ?: "webview"
        val transport = Regex("\"transport\":\"([^\"]+)\"").find(message)?.groupValues?.get(1)
            ?: "webview-bridge"

        statusMessage = "Bridge event: ${event ?: "unknown"}"
        if (event == "loginSuccess" && email != null && name != null) {
            onSessionUpdate(
                DemoSession(
                    name = name,
                    email = email,
                    source = source,
                    transport = transport
                )
            )
            context.toast("WebView login captured for $email")
        } else if (event == "logout") {
            context.toast("WebView session cleared")
        }
    }

    BackHandler {
        if (webView.canGoBack()) webView.goBack() else onBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            CookieManager.getInstance().flush()
            val bundle = Bundle()
            webView.saveState(bundle)
            webViewState.bundle = bundle
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (webView.canGoBack()) webView.goBack() else onBack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF38BDF8)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pageTitle,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentUrl,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF555555),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { webView.reload() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = Color(0xFF38BDF8)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    color = Color(0xFF38BDF8).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "WEBVIEW LOGIN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 2.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AndroidBridge injected · compare against Custom Tabs SSO",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Color(0xFF444444)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricChip(label = "DOM", value = domReadyMs)
                MetricChip(label = "PAINT", value = firstMeaningfulPaintMs)
                MetricChip(label = "FULL", value = fullLoadMs)
            }

            Text(
                text = statusMessage,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFF6F6F6F)
            )

            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedButton(onClick = onBack) {
                    Text("Back to app")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    progress = { loadingProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF222222)
                )
            }
        }

        if (!LocalInspectionMode.current) {
            AndroidView(
                factory = { _ ->
                    webView.apply {
                        val webViewRef = this
                        addJavascriptInterface(
                            AndroidBridge(
                                onMessage = { message -> post { handleBridgeMessage(message) } },
                                onClose = { post { onBack() } }
                            ),
                            "AndroidBridge"
                        )

                        CookieManager.getInstance().apply {
                            setAcceptCookie(true)
                            setAcceptThirdPartyCookies(webViewRef, true)
                        }

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadsImagesAutomatically = true
                            mediaPlaybackRequiresUserGesture = false
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            javaScriptCanOpenWindowsAutomatically = true
                            setSupportMultipleWindows(false)
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                                loadStartedAtMs = SystemClock.elapsedRealtime()
                                isLoading = true
                                loadingProgress = 0
                                currentUrl = url
                                pageTitle = "Loading..."
                                domReadyMs = null
                                fullLoadMs = null
                                firstMeaningfulPaintMs = null
                                statusMessage = "WebView created page pipeline inside app process…"
                            }

                            override fun onPageFinished(view: WebView, url: String) {
                                isLoading = false
                                currentUrl = url
                                fullLoadMs = SystemClock.elapsedRealtime() - loadStartedAtMs
                                statusMessage = "HTML finished. JS bridge available as AndroidBridge."
                                CookieManager.getInstance().flush()

                                view.evaluateJavascript(
                                    """
                                    (function() {
                                        var nav = performance.getEntriesByType('navigation')[0];
                                        var dom = nav ? Math.round(nav.domContentLoadedEventEnd) : -1;
                                        var load = nav ? Math.round(nav.loadEventEnd) : -1;
                                        var paints = performance.getEntriesByType('paint');
                                        var fcp = -1;
                                        for (var i = 0; i < paints.length; i++) {
                                            if (paints[i].name === 'first-contentful-paint') {
                                                fcp = Math.round(paints[i].startTime);
                                            }
                                        }
                                        return JSON.stringify({dom: dom, load: load, fcp: fcp});
                                    })();
                                    """.trimIndent()
                                ) { result ->
                                    parsePerformanceMetrics(result)?.let { metrics ->
                                        if (metrics.domMs >= 0) domReadyMs = metrics.domMs
                                        if (metrics.loadMs >= 0) fullLoadMs = metrics.loadMs
                                        if (metrics.firstContentfulPaintMs >= 0) {
                                            firstMeaningfulPaintMs = metrics.firstContentfulPaintMs
                                        }
                                    }

                                    if (lastLoadedUrl != url) {
                                        lastLoadedUrl = url
                                        showMetricsToast("WebView metrics captured")
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val uri = request.url
                                val session = extractSessionFromUri(uri)
                                if (session != null) {
                                    onSessionUpdate(session)
                                    onBack()
                                    return true
                                }
                                val scheme = uri.scheme ?: ""
                                return scheme != "http" && scheme != "https"
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView, title: String) {
                                pageTitle = title
                            }

                            override fun onProgressChanged(view: WebView, newProgress: Int) {
                                loadingProgress = newProgress
                                statusMessage = when {
                                    newProgress < 25 -> "Spinning up renderer, network stack, and page scripts…"
                                    newProgress < 60 -> "Fetching HTML/CSS/JS inside WebView…"
                                    newProgress < 90 -> "Executing scripts and painting UI…"
                                    newProgress < 100 -> "Final resources loading…"
                                    else -> "Page visually ready. Compare this feeling against Custom Tabs."
                                }
                                if (newProgress == 100) isLoading = false
                            }
                        }

                        if (webViewState.bundle != null) {
                            restoreState(webViewState.bundle!!)
                        } else {
                            loadUrl(url)
                        }
                    }
                },
                update = { },
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            )
        } else {
            Box(Modifier.fillMaxSize()) {
                Text("webview preview", Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: Long?) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        color = Color(0xFF181818)
    ) {
        Text(
            text = "$label ${value?.let { "${it}ms" } ?: "--"}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFF9DD7FF)
        )
    }
}

private data class PerformanceMetrics(
    val domMs: Long,
    val loadMs: Long,
    val firstContentfulPaintMs: Long
)

private fun parsePerformanceMetrics(result: String): PerformanceMetrics? {
    val cleaned = result.removePrefix("\"").removeSuffix("\"").replace("\\\"", "\"")
    val dom = Regex("\"dom\":(-?\\d+)").find(cleaned)?.groupValues?.get(1)?.toLongOrNull() ?: return null
    val load = Regex("\"load\":(-?\\d+)").find(cleaned)?.groupValues?.get(1)?.toLongOrNull() ?: -1L
    val fcp = Regex("\"fcp\":(-?\\d+)").find(cleaned)?.groupValues?.get(1)?.toLongOrNull() ?: -1L
    return PerformanceMetrics(domMs = dom, loadMs = load, firstContentfulPaintMs = fcp)
}

@Preview(showBackground = true)
@Composable
private fun WebViewScreenPreview() {
    WebViewScreen(
        url = "http://10.0.2.2:3000",
        onBack = {},
        onSessionUpdate = {}
    )
}

@Composable
fun rememberWebViewState(): WebViewState {
    val context = LocalContext.current
    return remember {
        WebViewState(WebView(context))
    }
}
