package com.shubham.androidcustomtabs

import android.content.Context
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

private const val DEFAULT_URL = "http://10.0.2.2:3000"

data class DemoSession(
    val name: String,
    val email: String,
    val source: String,
    val transport: String
)

@Composable
fun BrowserDemoApp() {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val keyboardController = LocalSoftwareKeyboardController.current

    var baseUrl by remember { mutableStateOf(DEFAULT_URL) }
    var showWebView by remember { mutableStateOf(false) }
    var webViewUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var currentSession by remember { mutableStateOf<DemoSession?>(null) }

    val customTabsManager = remember(context) { CustomTabsPerformanceManager(context) }

    DisposableEffect(customTabsManager) {
        customTabsManager.bind()
        onDispose { customTabsManager.unbind() }
    }

    LaunchedEffect(activity?.deepLinkIntent) {
        extractSessionFromUri(activity?.deepLinkIntent?.data)?.let {
            currentSession = it
            errorMessage = ""
            context.toast("Custom Tabs returned to app for ${it.email}")
        }
    }

    if (showWebView) {
        WebViewScreen(
            url = webViewUrl,
            onBack = { showWebView = false },
            onSessionUpdate = { currentSession = it }
        )
    } else {
        HomeScreen(
            url = baseUrl,
            onUrlChange = {
                baseUrl = it
                errorMessage = ""
            },
            errorMessage = errorMessage,
            session = currentSession,
            onCustomTabsClick = {
                val result = openWithCustomTabs(
                    keyboardController = keyboardController,
                    context = context,
                    url = buildCustomTabsLoginUrl(baseUrl),
                    performanceManager = customTabsManager
                )
                errorMessage = result ?: ""
            },
            onWebViewClick = {
                val result = openWithWebView(
                    keyboardController = keyboardController,
                    url = buildLoginDemoUrl(baseUrl)
                )
                if (result.errorMessage != null) {
                    errorMessage = result.errorMessage
                } else if (result.webViewUrl != null) {
                    webViewUrl = result.webViewUrl
                    showWebView = true
                }
            },
            onClearSession = { currentSession = null }
        )
    }
}

fun buildCustomTabsLoginUrl(input: String): String {
    val base = buildLoginDemoUrl(input)
    return "${base}?callbackUrl=androidcustomtabs%3A%2F%2Flogin"
}

fun openWithCustomTabs(
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    url: String,
    performanceManager: CustomTabsPerformanceManager
): String? {
    val finalUrl = normalizeUrl(url)
    val targetUri = finalUrl.toUri()
    return try {
        if (!performanceManager.isSupported()) {
            return "No Custom Tabs supporting browser found on this device."
        }

        performanceManager.prepareUrl(targetUri)

        val primaryColor = ContextCompat.getColor(context, R.color.teal_200)
        val secondaryColor = ContextCompat.getColor(context, R.color.purple_200)
        val colorParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(primaryColor)
            .build()

        val builder = CustomTabsIntent.Builder(performanceManager.getSession())
            .setShowTitle(true)
            .setDefaultColorSchemeParams(colorParams)
            .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
            .setUrlBarHidingEnabled(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setInstantAppsEnabled(false)
            .setStartAnimations(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .setExitAnimations(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )

        val customTabsIntent = builder.build().apply {
            intent.`package` = performanceManager.getPackageName()
            intent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, true)
        }

        performanceManager.markLaunchStarted()
        customTabsIntent.launchUrl(context, targetUri)
        keyboardController?.hide()

        context.toast(
            "Launching SSO demo with Custom Tabs. Browser session stays outside the app and returns via deep link."
        )
        null
    } catch (e: Exception) {
        keyboardController?.hide()
        "Could not open Custom Tab: ${e.localizedMessage ?: "Unknown error"}"
    }
}

data class WebViewResult(
    val webViewUrl: String? = null,
    val errorMessage: String? = null
)

fun openWithWebView(
    keyboardController: SoftwareKeyboardController?,
    url: String
): WebViewResult {
    val finalUrl = normalizeUrl(url)
    keyboardController?.hide()
    return WebViewResult(webViewUrl = finalUrl)
}

@Preview(showBackground = true, name = "BrowserDemoApp - Home")
@Composable
private fun BrowserDemoAppPreview_Home() {
    BrowserDemoApp()
}
